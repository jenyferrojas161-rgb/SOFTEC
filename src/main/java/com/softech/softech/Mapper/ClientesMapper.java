package com.softech.softech.Mapper;

import com.softech.softech.DTOs.ClientesDTO;
import com.softech.softech.modelo.clientes;

public class ClientesMapper {

    public static ClientesDTO toDTO(clientes entidad) {
        return new ClientesDTO(
                entidad.getId(),
                entidad.getIdUsuario(),
                entidad.getNombreCliente()
        );
    }

    public static clientes toEntity(ClientesDTO dto) {
        clientes entidad = new clientes();
        entidad.setId(dto.getId());
        entidad.setIdUsuario(dto.getIdUsuario());
        entidad.setNombreCliente(dto.getNombreCliente());
        return entidad;
    }
}
