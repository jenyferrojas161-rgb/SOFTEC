package com.softech.softech.Mapper;

import com.softech.softech.DTOs.ServicioTecnicoDTO;
import com.softech.softech.modelo.ServicioTecnicoId;
import com.softech.softech.modelo.servicioTecnico;

public class ServicioTecnicoMapper {

    public static ServicioTecnicoDTO toDTO(servicioTecnico entidad) {
        return new ServicioTecnicoDTO(
                entidad.getId().getIdServicio(),
                entidad.getId().getIdTecnico(),
                entidad.getFechaAsignacion()
        );
    }

    public static servicioTecnico toEntity(ServicioTecnicoDTO dto) {
        servicioTecnico entidad = new servicioTecnico();
        entidad.setId(new ServicioTecnicoId(dto.getIdServicio(), dto.getIdTecnico()));
        entidad.setFechaAsignacion(dto.getFechaAsignacion());
        return entidad;
    }
}
