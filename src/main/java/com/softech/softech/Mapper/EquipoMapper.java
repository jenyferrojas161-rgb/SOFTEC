package com.softech.softech.Mapper;

import com.softech.softech.DTOs.EquipoDTO;
import com.softech.softech.modelo.equipo;

public class EquipoMapper {

    public static EquipoDTO toDTO(equipo entidad) {
        return new EquipoDTO(
                entidad.getId(),
                entidad.getIdCliente(),
                entidad.getTipoEquipo(),
                entidad.getMarca(),
                entidad.getModelo(),
                entidad.getNumeroSerie(),
                entidad.getEstadoEquipo()
        );
    }

    public static equipo toEntity(EquipoDTO dto) {
        equipo entidad = new equipo();
        entidad.setId(dto.getId());
        entidad.setIdCliente(dto.getIdCliente());
        entidad.setTipoEquipo(dto.getTipoEquipo());
        entidad.setMarca(dto.getMarca());
        entidad.setModelo(dto.getModelo());
        entidad.setNumeroSerie(dto.getNumeroSerie());
        entidad.setEstadoEquipo(dto.getEstadoEquipo());
        return entidad;
    }
}
