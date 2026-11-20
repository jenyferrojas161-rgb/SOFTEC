package com.softech.softech.Servicios;

import com.softech.softech.DTOs.SolicitudServicioRequest;
import com.softech.softech.Mapper.ServiciosMapper;
import com.softech.softech.modelo.clientes;
import com.softech.softech.modelo.equipo;
import com.softech.softech.modelo.servicios;
import com.softech.softech.modelo.usuario;
import com.softech.softech.repositorio.reposi_clientes;
import com.softech.softech.repositorio.reposi_equipo;
import com.softech.softech.repositorio.reposi_servicios;
import com.softech.softech.repositorio.reposi_usuario;
import com.softech.softech.security.CurrentUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class SolicitudServicioServicio {

    private static final Set<String> PRIORIDADES = Set.of("Baja", "Media", "Alta", "Urgente");
    private static final Set<String> TIPOS = Set.of("Mantenimiento", "Reparación", "Diagnóstico", "Instalación");

    private final reposi_usuario usuarios;
    private final reposi_clientes clientesRepo;
    private final reposi_equipo equiposRepo;
    private final reposi_servicios serviciosRepo;
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final SecureRandom azar = new SecureRandom();

    public SolicitudServicioServicio(reposi_usuario usuarios, reposi_clientes clientesRepo, reposi_equipo equiposRepo,
                                     reposi_servicios serviciosRepo, JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.clientesRepo = clientesRepo;
        this.equiposRepo = equiposRepo;
        this.serviciosRepo = serviciosRepo;
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Transactional
    public Map<String, Object> crear(SolicitudServicioRequest r) {
        if (vacio(r.equipoTipo())) throw new IllegalArgumentException("Escribe el tipo de equipo (por ejemplo: Portátil, PC de escritorio, Impresora).");
        if (vacio(r.problema())) throw new IllegalArgumentException("Describe el problema reportado.");
        if (vacio(r.tipoServicio()) || !TIPOS.contains(r.tipoServicio().trim())) throw new IllegalArgumentException("Selecciona el tipo de servicio.");
        String prioridad = vacio(r.prioridad()) ? "Media" : r.prioridad().trim();
        if (!PRIORIDADES.contains(prioridad)) throw new IllegalArgumentException("Prioridad no válida.");

        String claveTemporal = null;
        boolean clienteNuevo = false;
        clientes cli;

        if (r.idCliente() != null) {
            cli = clientesRepo.findById(r.idCliente()).orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado ya no existe."));
        } else {
            if (vacio(r.clienteNombre())) throw new IllegalArgumentException("Escribe el nombre del cliente.");
            if (vacio(r.clienteDocumento()) || !r.clienteDocumento().trim().matches("^[0-9]{6,12}$")) throw new IllegalArgumentException("El documento debe tener solo números (6 a 12 dígitos).");
            if (vacio(r.clienteEmail()) || !r.clienteEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Escribe un correo electrónico válido para el cliente.");

            String documento = r.clienteDocumento().trim();
            String email = r.clienteEmail().trim().toLowerCase(Locale.ROOT);

            usuario existente = usuarios.findByIdentificacion(documento).orElse(null);
            if (existente != null) {
                // Ese documento ya está en el sistema: se reutiliza al mismo cliente (sin duplicarlo).
                cli = clientesRepo.findByIdUsuario(existente.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Ese documento pertenece a una cuenta que no es de cliente."));
            } else {
                if (usuarios.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Ese correo ya está registrado con otro documento.");
                claveTemporal = generarClave();
                usuario u = new usuario();
                u.setIdentificacion(documento);
                u.setNombreUsuario(r.clienteNombre().trim());
                u.setTelefono(vacio(r.clienteTelefono()) ? null : r.clienteTelefono().trim());
                u.setEmail(email);
                u.setContrasena(encoder.encode(claveTemporal));
                u.setTipoUsuario("Cliente");
                u = usuarios.save(u);

                cli = new clientes();
                cli.setIdUsuario(u.getId());
                cli.setNombreCliente(r.clienteNombre().trim());
                cli = clientesRepo.save(cli);
                clienteNuevo = true;
            }
        }

        equipo eq = new equipo();
        eq.setIdCliente(cli.getId());
        eq.setTipoEquipo(r.equipoTipo().trim());
        eq.setMarca(vacio(r.equipoMarca()) ? null : r.equipoMarca().trim());
        eq.setModelo(vacio(r.equipoModelo()) ? null : r.equipoModelo().trim());
        eq.setNumeroSerie(vacio(r.equipoSerie()) ? null : r.equipoSerie().trim());
        eq.setEstadoEquipo("Recibido");
        eq = equiposRepo.save(eq);

        servicios sv = new servicios();
        sv.setIdEquipo(eq.getId());
        sv.setIdUsuario(cli.getIdUsuario());
        sv.setFechaIngresoEquipo(LocalDate.now());
        sv.setProblemaReportado(r.problema().trim());
        sv.setEstadoServicio("Pendiente");
        sv.setPrioridad(prioridad);
        sv.setTipoServicio(r.tipoServicio().trim());
        sv = serviciosRepo.save(sv);

        if (r.idTecnico() != null) {
            Long existe = jdbc.queryForObject("SELECT COUNT(*) FROM tecnicos WHERE id_tecnico = ?", Long.class, r.idTecnico());
            if (existe == null || existe == 0) throw new IllegalArgumentException("El técnico seleccionado ya no existe.");
            jdbc.update("INSERT INTO servicio_tecnico (id_servicio, id_tecnico) VALUES (?, ?)", sv.getId(), r.idTecnico());
        }

        String descEquipo = (eq.getTipoEquipo() + " " + (eq.getMarca() == null ? "" : eq.getMarca()) + " " + (eq.getModelo() == null ? "" : eq.getModelo())).trim();
        Long idActor = CurrentUser.id();
        jdbc.update("INSERT INTO servicio_historial (id_servicio, id_usuario, estado_anterior, estado_nuevo, nota) VALUES (?, ?, NULL, ?, ?)",
                sv.getId(), idActor == null ? null : idActor.intValue(), "Pendiente",
                "Solicitud registrada. Equipo recibido: " + descEquipo + (r.idTecnico() != null ? ". Técnico asignado." : "."));

        Map<String, Object> out = new HashMap<>();
        out.put("servicio", ServiciosMapper.toDTO(sv));
        out.put("idCliente", cli.getId());
        out.put("idEquipo", eq.getId());
        out.put("clienteNuevo", clienteNuevo);
        out.put("claveTemporal", claveTemporal);
        return out;
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    /** Cumple la política: 10+ caracteres, mayúscula, minúscula, número y carácter especial. */
    private String generarClave() {
        String may = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String min = "abcdefghjkmnpqrstuvwxyz";
        String num = "23456789";
        String esp = "@#$%&*!";
        String todo = may + min + num;
        StringBuilder sb = new StringBuilder();
        sb.append(may.charAt(azar.nextInt(may.length())));
        sb.append(min.charAt(azar.nextInt(min.length())));
        sb.append(num.charAt(azar.nextInt(num.length())));
        sb.append(esp.charAt(azar.nextInt(esp.length())));
        for (int i = 0; i < 8; i++) sb.append(todo.charAt(azar.nextInt(todo.length())));
        return sb.toString();
    }
}
