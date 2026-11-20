package com.softech.softech.DTOs;

import java.time.LocalDate;

public class ServicioTecnicoDTO {
    private Long idServicio;
    private Long idTecnico;
    private LocalDate fechaAsignacion;

    public ServicioTecnicoDTO(Long idServicio, Long idTecnico, LocalDate fechaAsignacion) {
        this.idServicio = idServicio;
        this.idTecnico = idTecnico;
        this.fechaAsignacion = fechaAsignacion;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public Long getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Long idTecnico) {
        this.idTecnico = idTecnico;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    @Override
    public String toString() {
        return "ServicioTecnicoDTO{" +
                "idServicio=" + idServicio +
                ", idTecnico=" + idTecnico +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }
}
