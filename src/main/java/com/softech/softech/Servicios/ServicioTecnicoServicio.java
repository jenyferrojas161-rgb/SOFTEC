package com.softech.softech.Servicios;

import com.softech.softech.DTOs.EstadoServicioDTO;
import com.softech.softech.DTOs.ServicioTecnicoDTO;
import com.softech.softech.Mapper.ServicioTecnicoMapper;
import com.softech.softech.modelo.ServicioTecnicoId;
import com.softech.softech.modelo.servicioTecnico;
import com.softech.softech.notificaciones.NotificationService;
import com.softech.softech.repositorio.reposi_servicioTecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioTecnicoServicio {

    @Autowired
    private reposi_servicioTecnico repo;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private NotificationService notifications;

    public List<ServicioTecnicoDTO> listar() {
        return repo.findAll().stream()
                .map(ServicioTecnicoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ServicioTecnicoDTO insertar(ServicioTecnicoDTO dto) {
        servicioTecnico entidad = ServicioTecnicoMapper.toEntity(dto);
        servicioTecnico guardado = repo.save(entidad);
        return ServicioTecnicoMapper.toDTO(guardado);
    }

    public ServicioTecnicoDTO actualizar(Long idServicio, Long idTecnico, ServicioTecnicoDTO dto) {
        ServicioTecnicoId id = new ServicioTecnicoId(idServicio, idTecnico);
        Optional<servicioTecnico> existe = repo.findById(id);
        if (existe.isPresent()) {
            servicioTecnico existente = existe.get();
            existente.setFechaAsignacion(dto.getFechaAsignacion());
            servicioTecnico actualizado = repo.save(existente);
            return ServicioTecnicoMapper.toDTO(actualizado);
        }
        throw new RuntimeException("Asignación no encontrada para servicio " + idServicio + " y técnico " + idTecnico);
    }

    public String eliminar(Long idServicio, Long idTecnico) {
        ServicioTecnicoId id = new ServicioTecnicoId(idServicio, idTecnico);
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Asignación eliminada correctamente";
        }
        return "Asignación no encontrada para servicio " + idServicio + " y técnico " + idTecnico;
    }

    public EstadoServicioDTO buscarEstado(String orden, String cedula) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.id_servicio, e.id_equipo, e.numero_serie, e.tipo_equipo,
                       e.marca, e.modelo, e.estado_equipo, s.estado_servicio,
                       s.problema_reportado, s.diagnostico_realizado,
                       s.fecha_ingreso_equipo, s.fecha_entrega_equipo
                FROM servicios s
                JOIN equipo e ON e.id_equipo = s.id_equipo
                JOIN clientes c ON c.id_cliente = e.id_cliente
                JOIN usuario u ON u.id_usuario = c.id_usuario
                WHERE 1=1
                """);
        java.util.List<Object> args = new java.util.ArrayList<>();
        if (orden != null && !orden.isBlank()) {
            sql.append(" AND CAST(s.id_servicio AS TEXT) = ?");
            args.add(orden.trim());
        }
        if (cedula != null && !cedula.isBlank()) {
            sql.append(" AND u.identificacion = ?");
            args.add(cedula.trim());
        }
        sql.append(" ORDER BY s.id_servicio DESC LIMIT 1");

        var filas = jdbc.query(sql.toString(), args.toArray(), (rs, rowNum) -> new EstadoServicioDTO(
                rs.getLong("id_servicio"), rs.getLong("id_equipo"), rs.getString("numero_serie"),
                rs.getString("tipo_equipo"), rs.getString("marca"), rs.getString("modelo"),
                rs.getString("estado_equipo"), rs.getString("estado_servicio"),
                rs.getString("problema_reportado"), rs.getString("diagnostico_realizado"),
                rs.getString("fecha_ingreso_equipo"), rs.getString("fecha_entrega_equipo")
        ));
        return filas.isEmpty() ? null : filas.get(0);
    }
}
