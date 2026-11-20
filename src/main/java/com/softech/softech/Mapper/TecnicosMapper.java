package com.softech.softech.Mapper;

import com.softech.softech.DTOs.TecnicosDTO;
import com.softech.softech.modelo.tecnicos;

public class TecnicosMapper {

    public static TecnicosDTO toDTO(tecnicos entidad) {
        return new TecnicosDTO(
                entidad.getId(),
                entidad.getIdUsuario(),
                entidad.getEspecialidad(),
                entidad.getTurno(),
                entidad.getEstadoDisponibilidad()
        );
    }

    public static tecnicos toEntity(TecnicosDTO dto) {
        tecnicos entidad = new tecnicos();
        entidad.setId(dto.getId());
        entidad.setIdUsuario(dto.getIdUsuario());
        entidad.setEspecialidad(dto.getEspecialidad());
        entidad.setTurno(dto.getTurno());
        entidad.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
        return entidad;
    }
}
