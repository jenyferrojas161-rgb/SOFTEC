package com.softech.softech.Mapper;

import com.softech.softech.DTOs.UsuarioDTO;
import com.softech.softech.modelo.usuario;

public class UsuarioMapper {
    public static UsuarioDTO toDTO(usuario entidad) {
        return new UsuarioDTO(
                entidad.getId(),
                entidad.getIdentificacion(),
                entidad.getNombreUsuario(),
                entidad.getTelefono(),
                entidad.getEmail(),
                null,
                entidad.getTipoUsuario()
        );
    }

    public static usuario toEntity(UsuarioDTO dto) {
        usuario entidad = new usuario();
        entidad.setId(dto.getId());
        entidad.setIdentificacion(dto.getIdentificacion());
        entidad.setNombreUsuario(dto.getNombreUsuario());
        entidad.setTelefono(dto.getTelefono());
        entidad.setEmail(dto.getEmail());
        entidad.setContrasena(dto.getContrasena());
        entidad.setTipoUsuario(dto.getTipoUsuario());
        return entidad;
    }
}
