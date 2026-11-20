package com.softech.softech.Servicios;

import com.softech.softech.DTOs.DetallesFacturacionDTO;
import com.softech.softech.Mapper.DetallesFacturacionMapper;
import com.softech.softech.modelo.detallesFacturacion;
import com.softech.softech.repositorio.reposi_detallesFacturacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetallesFacturacionServicio {

    @Autowired
    private reposi_detallesFacturacion repo;

    public List<DetallesFacturacionDTO> listar() {
        return repo.findAll().stream()
                .map(DetallesFacturacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DetallesFacturacionDTO insertar(DetallesFacturacionDTO dto) {
        detallesFacturacion entidad = DetallesFacturacionMapper.toEntity(dto);
        detallesFacturacion guardado = repo.save(entidad);
        return DetallesFacturacionMapper.toDTO(guardado);
    }

    public DetallesFacturacionDTO actualizar(Long id, DetallesFacturacionDTO dto) {
        Optional<detallesFacturacion> existe = repo.findById(id);
        if (existe.isPresent()) {
            detallesFacturacion existente = existe.get();
            existente.setIdFacturacion(dto.getIdFacturacion());
            existente.setDescripcion(dto.getDescripcion());
            existente.setCantidad(dto.getCantidad());
            existente.setPrecio(dto.getPrecio());
            existente.setTotal(dto.getTotal());
            detallesFacturacion actualizado = repo.save(existente);
            return DetallesFacturacionMapper.toDTO(actualizado);
        } else {
            throw new RuntimeException("Detalle de facturacion no encontrado con id " + id);
        }
    }

    public String eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Detalle de facturacion eliminado correctamente";
        } else {
            return "Detalle de facturacion con id " + id + " no encontrado";
        }
    }
}
