package com.softech.softech.DTOs;

public class ClientesDTO {
    private Long id;
    private Long idUsuario;
    private String nombreCliente;

    public ClientesDTO(Long id, Long idUsuario, String nombreCliente) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreCliente = nombreCliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    @Override
    public String toString() {
        return "ClientesDTO{" + "id=" + id + ", " + "idUsuario=" + idUsuario + ", " + "nombreCliente=" + nombreCliente + "}";
    }
}
