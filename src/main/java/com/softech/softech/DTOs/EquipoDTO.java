package com.softech.softech.DTOs;


public class EquipoDTO {
    private Long id;
    private Long idCliente;
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String estadoEquipo;

    public EquipoDTO(Long id, Long idCliente, String tipoEquipo, String marca, String modelo, String numeroSerie, String estadoEquipo) {
        this.id = id;
        this.idCliente = idCliente;
        this.tipoEquipo = tipoEquipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.estadoEquipo = estadoEquipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getEstadoEquipo() {
        return estadoEquipo;
    }

    public void setEstadoEquipo(String estadoEquipo) {
        this.estadoEquipo = estadoEquipo;
    }

    @Override
    public String toString() {
        return "EquipoDTO{" + "id=" + id + ", " + "idCliente=" + idCliente + ", " + "tipoEquipo=" + tipoEquipo + ", " + "marca=" + marca + ", " + "modelo=" + modelo + ", " + "numeroSerie=" + numeroSerie + ", " + "estadoEquipo=" + estadoEquipo + "}";
    }
}
