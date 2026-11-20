package com.softech.softech.Servicios;

import com.softech.softech.DTOs.EquipoDTO;
import com.softech.softech.Mapper.EquipoMapper;
import com.softech.softech.modelo.equipo;
import com.softech.softech.repositorio.reposi_equipo;
import com.softech.softech.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoServicio {
    @Autowired private reposi_equipo repo;
    @Autowired private JdbcTemplate jdbc;

    public List<EquipoDTO> listar() {
        Long userId = CurrentUser.id();
        String role = CurrentUser.role();
        if (userId != null && "TECNICO".equals(role)) {
            List<Long> ids = jdbc.queryForList("""
                    SELECT DISTINCT s.id_equipo
                    FROM servicio_tecnico st
                    JOIN servicios s ON s.id_servicio = st.id_servicio
                    JOIN tecnicos t ON t.id_tecnico = st.id_tecnico
                    WHERE t.id_usuario = ?
                    """, Long.class, userId);
            return repo.findAllById(ids).stream().map(EquipoMapper::toDTO).collect(Collectors.toList());
        }
        if (userId != null && "CLIENTE".equals(role)) {
            List<Long> ids = jdbc.queryForList("""
                    SELECT e.id_equipo
                    FROM equipo e
                    JOIN clientes c ON c.id_cliente = e.id_cliente
                    WHERE c.id_usuario = ?
                    """, Long.class, userId);
            return repo.findAllById(ids).stream().map(EquipoMapper::toDTO).collect(Collectors.toList());
        }
        return repo.findAll().stream().map(EquipoMapper::toDTO).collect(Collectors.toList());
    }

    public EquipoDTO insertar(EquipoDTO dto) { return EquipoMapper.toDTO(repo.save(EquipoMapper.toEntity(dto))); }

    public EquipoDTO actualizar(Long id, EquipoDTO dto) {
        equipo existente = repo.findById(id).orElseThrow(() -> new RuntimeException("Equipo no encontrado con id " + id));
        existente.setIdCliente(dto.getIdCliente()); existente.setTipoEquipo(dto.getTipoEquipo()); existente.setMarca(dto.getMarca());
        existente.setModelo(dto.getModelo()); existente.setNumeroSerie(dto.getNumeroSerie()); existente.setEstadoEquipo(dto.getEstadoEquipo());
        return EquipoMapper.toDTO(repo.save(existente));
    }

    public String eliminar(Long id) {
        if (!repo.existsById(id)) return "Equipo con id " + id + " no encontrado";
        repo.deleteById(id); return "Equipo eliminado correctamente";
    }
}
