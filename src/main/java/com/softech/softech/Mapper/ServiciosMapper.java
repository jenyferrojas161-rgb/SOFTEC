package com.softech.softech.Mapper;

import com.softech.softech.DTOs.ServiciosDTO;
import com.softech.softech.modelo.servicios;

public class ServiciosMapper {

    public static ServiciosDTO toDTO(servicios entidad) {
        return new ServiciosDTO(
                entidad.getId(),
                entidad.getIdEquipo(),
                entidad.getIdUsuario(),
                entidad.getFechaIngresoEquipo(),
                entidad.getFechaEntregaEquipo(),
                entidad.getProblemaReportado(),
                entidad.getDiagnosticoRealizado(),
                entidad.getEstadoServicio(),
                entidad.getDescripcionProblema(),
                entidad.getPrioridad(),
                entidad.getTipoServicio()
        );
    }

    public static servicios toEntity(ServiciosDTO dto) {
        servicios entidad = new servicios();
        entidad.setId(dto.getId());
        entidad.setIdEquipo(dto.getIdEquipo());
        entidad.setIdUsuario(dto.getIdUsuario());
        entidad.setFechaIngresoEquipo(dto.getFechaIngresoEquipo());
        entidad.setFechaEntregaEquipo(dto.getFechaEntregaEquipo());
        entidad.setProblemaReportado(dto.getProblemaReportado());
        entidad.setDiagnosticoRealizado(dto.getDiagnosticoRealizado());
        entidad.setEstadoServicio(dto.getEstadoServicio());
        entidad.setDescripcionProblema(dto.getDescripcionProblema());
        entidad.setPrioridad(dto.getPrioridad());
        entidad.setTipoServicio(dto.getTipoServicio());
        return entidad;
    }
}
