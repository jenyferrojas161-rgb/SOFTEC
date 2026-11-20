package com.softech.softech.Servicios;

import com.softech.softech.DTOs.ServiciosDTO;
import com.softech.softech.Mapper.ServiciosMapper;
import com.softech.softech.modelo.servicios;
import com.softech.softech.notificaciones.NotificationService;
import com.softech.softech.repositorio.reposi_servicios;
import com.softech.softech.security.CurrentUser;
import com.softech.softech.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServicioServicio {

    @Autowired private reposi_servicios repo;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private NotificationService notifications;

    public List<ServiciosDTO> listar() {
        Long userId = CurrentUser.id();
        String role = CurrentUser.role();
        if (userId != null && "TECNICO".equals(role)) {
            List<Long> ids = jdbc.queryForList("""
                    SELECT st.id_servicio
                    FROM servicio_tecnico st
                    JOIN tecnicos t ON t.id_tecnico = st.id_tecnico
                    WHERE t.id_usuario = ?
                    """, Long.class, userId);
            return repo.findAllById(ids).stream().map(ServiciosMapper::toDTO).collect(Collectors.toList());
        }
        if (userId != null && "CLIENTE".equals(role)) {
            List<Long> ids = jdbc.queryForList("""
                    SELECT s.id_servicio
                    FROM servicios s
                    JOIN equipo e ON e.id_equipo = s.id_equipo
                    JOIN clientes c ON c.id_cliente = e.id_cliente
                    WHERE c.id_usuario = ?
                    """, Long.class, userId);
            return repo.findAllById(ids).stream().map(ServiciosMapper::toDTO).collect(Collectors.toList());
        }
        return repo.findAll().stream().map(ServiciosMapper::toDTO).collect(Collectors.toList());
    }

    public ServiciosDTO insertar(ServiciosDTO dto) {
        servicios entidad = ServiciosMapper.toEntity(dto);
        servicios guardado = repo.save(entidad);
        registrarHistorial(guardado.getId(), null, guardado.getEstadoServicio(), "Solicitud registrada.");
        return ServiciosMapper.toDTO(guardado);
    }

    @Transactional
    public ServiciosDTO actualizar(Long id, ServiciosDTO dto) {
        servicios existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id " + id));

        String estadoAnterior = existente.getEstadoServicio();
        existente.setIdEquipo(dto.getIdEquipo());
        existente.setIdUsuario(dto.getIdUsuario());
        existente.setFechaIngresoEquipo(dto.getFechaIngresoEquipo());
        existente.setFechaEntregaEquipo(dto.getFechaEntregaEquipo());
        existente.setProblemaReportado(dto.getProblemaReportado());
        existente.setDiagnosticoRealizado(dto.getDiagnosticoRealizado());
        existente.setEstadoServicio(dto.getEstadoServicio());
        existente.setDescripcionProblema(dto.getDescripcionProblema());
        existente.setPrioridad(dto.getPrioridad());
        existente.setTipoServicio(dto.getTipoServicio());

        servicios actualizado = repo.save(existente);
        registrarHistorial(id, estadoAnterior, actualizado.getEstadoServicio(), "Solicitud editada por la administración.");
        notificarCambio(id, estadoAnterior, actualizado.getEstadoServicio());
        return ServiciosMapper.toDTO(actualizado);
    }

    @Transactional
    public ServiciosDTO actualizarEstado(Long id, String estado) {
        return seguimiento(id, estado, null, null);
    }

    private static final Set<String> ESTADOS = Set.of("Pendiente", "En_Proceso", "Finalizado", "Entregado", "Cancelado");

    /** Cambio de estado + diagnóstico + nota, con registro en el historial del servicio. */
    @Transactional
    public ServiciosDTO seguimiento(Long id, String estado, String diagnostico, String nota) {
        if (estado == null || !ESTADOS.contains(estado)) throw new IllegalArgumentException("Estado de servicio no válido.");
        servicios existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id " + id));
        if ("TECNICO".equals(CurrentUser.role())) {
            Long asignado = jdbc.query("""
                    SELECT COUNT(*) FROM servicio_tecnico st
                    JOIN tecnicos t ON t.id_tecnico = st.id_tecnico
                    WHERE st.id_servicio = ? AND t.id_usuario = ?
                    """, ps -> { ps.setLong(1, id); ps.setLong(2, CurrentUser.id()); },
                    rs -> { rs.next(); return rs.getLong(1); });
            if (asignado == 0) throw new org.springframework.security.access.AccessDeniedException("El servicio no está asignado a este técnico.");
        }
        String anterior = existente.getEstadoServicio();
        existente.setEstadoServicio(estado);
        if (diagnostico != null && !diagnostico.isBlank()) existente.setDiagnosticoRealizado(diagnostico.trim());
        if ("Finalizado".equals(estado) || "Entregado".equals(estado)) {
            existente.setFechaEntregaEquipo(LocalDate.now());
        }
        servicios actualizado = repo.save(existente);
        String detalle = nota == null ? "" : nota.trim();
        if (diagnostico != null && !diagnostico.isBlank()) {
            detalle = (detalle.isEmpty() ? "" : detalle + " · ") + "Diagnóstico: " + diagnostico.trim();
        }
        registrarHistorial(id, anterior, estado, detalle.isEmpty() ? null : detalle);
        notificarCambio(id, anterior, estado);
        return ServiciosMapper.toDTO(actualizado);
    }

    /** Línea de tiempo del servicio. Un técnico solo ve la de sus servicios; un cliente, la de los suyos. */
    public List<Map<String, Object>> historial(Long idServicio) {
        verificarAcceso(idServicio);
        return jdbc.queryForList("""
                SELECT h.id_historial AS "id", h.estado_anterior AS "estadoAnterior", h.estado_nuevo AS "estadoNuevo",
                       h.nota AS "nota", h.fecha_hora AS "fechaHora",
                       u.nombre_usuario AS "usuario", u.tipo_usuario AS "rol"
                FROM servicio_historial h
                LEFT JOIN usuario u ON u.id_usuario = h.id_usuario
                WHERE h.id_servicio = ?
                ORDER BY h.fecha_hora, h.id_historial
                """, idServicio);
    }

    private void verificarAcceso(Long idServicio) {
        String rol = CurrentUser.role();
        Long uid = CurrentUser.id();
        if ("TECNICO".equals(rol)) {
            Long n = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM servicio_tecnico st JOIN tecnicos t ON t.id_tecnico = st.id_tecnico
                    WHERE st.id_servicio = ? AND t.id_usuario = ?""", Long.class, idServicio, uid);
            if (n == null || n == 0) throw new org.springframework.security.access.AccessDeniedException("El servicio no está asignado a este técnico.");
        } else if ("CLIENTE".equals(rol)) {
            Long n = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM servicios s JOIN equipo e ON e.id_equipo = s.id_equipo
                    JOIN clientes c ON c.id_cliente = e.id_cliente
                    WHERE s.id_servicio = ? AND c.id_usuario = ?""", Long.class, idServicio, uid);
            if (n == null || n == 0) throw new org.springframework.security.access.AccessDeniedException("El servicio no pertenece a este cliente.");
        }
    }

    private void registrarHistorial(Long idServicio, String anterior, String nuevo, String nota) {
        try {
            Long uid = CurrentUser.id();
            jdbc.update("INSERT INTO servicio_historial (id_servicio, id_usuario, estado_anterior, estado_nuevo, nota) VALUES (?, ?, ?, ?, ?)",
                    idServicio, uid == null ? null : uid.intValue(), anterior, nuevo, nota);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(ServicioServicio.class).error("No se pudo registrar el historial del servicio {}", idServicio, e);
        }
    }

    public String eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Servicio eliminado correctamente";
        }
        return "Servicio con id " + id + " no encontrado";
    }

    private void notificarCambio(Long idServicio, String anterior, String nuevo) {
        if (nuevo == null || nuevo.equals(anterior)) return;
        List<Long> usuarios = jdbc.queryForList("""
                SELECT c.id_usuario
                FROM servicios s
                JOIN equipo e ON e.id_equipo = s.id_equipo
                JOIN clientes c ON c.id_cliente = e.id_cliente
                WHERE s.id_servicio = ?
                """, Long.class, idServicio);
        String tipo = ("Finalizado".equals(nuevo) || "Entregado".equals(nuevo)) ? "SERVICIO_FINALIZADO" : "SERVICIO_ACTUALIZADO";
        String mensaje = "El servicio #" + idServicio + " cambió de estado a " + nuevo.replace("_", " ") + ".";
        for (Long idUsuario : usuarios) notifications.notificar(idUsuario, tipo, mensaje);
    }
}
