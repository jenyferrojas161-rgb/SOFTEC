package com.softech.softech.Servicios;

import com.softech.softech.DTOs.TecnicosDTO;
import com.softech.softech.Mapper.TecnicosMapper;
import com.softech.softech.modelo.tecnicos;
import com.softech.softech.repositorio.reposi_tecnicos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TecnicoServicio {

    @Autowired
    private reposi_tecnicos repo;

    public List<TecnicosDTO> listar() {
        return repo.findAll().stream()
                .map(TecnicosMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TecnicosDTO insertar(TecnicosDTO dto) {
        tecnicos entidad = TecnicosMapper.toEntity(dto);
        tecnicos guardado = repo.save(entidad);
        return TecnicosMapper.toDTO(guardado);
    }

    public TecnicosDTO actualizar(Long id, TecnicosDTO dto) {
        Optional<tecnicos> existe = repo.findById(id);
        if (existe.isPresent()) {
            tecnicos existente = existe.get();
            existente.setIdUsuario(dto.getIdUsuario());
            existente.setEspecialidad(dto.getEspecialidad());
            existente.setTurno(dto.getTurno());
            existente.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
            tecnicos actualizado = repo.save(existente);
            return TecnicosMapper.toDTO(actualizado);
        } else {
            throw new RuntimeException("Tecnicos no encontrado con id " + id);
        }
    }

    public String eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Tecnicos eliminado correctamente";
        } else {
            return "Tecnicos con id " + id + " no encontrado";
        }
    }
}
