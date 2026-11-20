package com.softech.softech.Mapper;

import com.softech.softech.DTOs.FacturacionDTO;
import com.softech.softech.modelo.facturacion;

public class FacturacionMapper {

    public static FacturacionDTO toDTO(facturacion entidad) {
        return new FacturacionDTO(
                entidad.getId(),
                entidad.getIdServicio(),
                entidad.getIdCliente(),
                entidad.getNumeroFactura(),
                entidad.getFechaFactura(),
                entidad.getFechaPago(),
                entidad.getTipoComprobante(),
                entidad.getTotalPagar(),
                entidad.getMetodoPago(),
                entidad.getEstadoPago()
        );
    }

    public static facturacion toEntity(FacturacionDTO dto) {
        facturacion entidad = new facturacion();
        entidad.setId(dto.getId());
        entidad.setIdServicio(dto.getIdServicio());
        entidad.setIdCliente(dto.getIdCliente());
        entidad.setNumeroFactura(dto.getNumeroFactura());
        entidad.setFechaFactura(dto.getFechaFactura());
        entidad.setFechaPago(dto.getFechaPago());
        entidad.setTipoComprobante(dto.getTipoComprobante());
        entidad.setTotalPagar(dto.getTotalPagar());
        entidad.setMetodoPago(dto.getMetodoPago());
        entidad.setEstadoPago(dto.getEstadoPago());
        return entidad;
    }
}
