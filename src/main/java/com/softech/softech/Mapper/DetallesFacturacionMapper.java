package com.softech.softech.Mapper;

import com.softech.softech.DTOs.DetallesFacturacionDTO;
import com.softech.softech.modelo.detallesFacturacion;

public class DetallesFacturacionMapper {

    public static DetallesFacturacionDTO toDTO(detallesFacturacion entidad) {
        return new DetallesFacturacionDTO(
                entidad.getId(),
                entidad.getIdFacturacion(),
                entidad.getDescripcion(),
                entidad.getCantidad(),
                entidad.getPrecio(),
                entidad.getTotal()
        );
    }

    public static detallesFacturacion toEntity(DetallesFacturacionDTO dto) {
        detallesFacturacion entidad = new detallesFacturacion();
        entidad.setId(dto.getId());
        entidad.setIdFacturacion(dto.getIdFacturacion());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setCantidad(dto.getCantidad());
        entidad.setPrecio(dto.getPrecio());
        entidad.setTotal(dto.getTotal());
        return entidad;
    }
}
