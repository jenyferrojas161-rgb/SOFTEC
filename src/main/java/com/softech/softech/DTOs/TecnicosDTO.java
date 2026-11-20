package com.softech.softech.DTOs;


public class TecnicosDTO {
    private Long id;
    private Long idUsuario;
    private String especialidad;
    private String turno;
    private String estadoDisponibilidad;

    public TecnicosDTO(Long id, Long idUsuario, String especialidad, String turno, String estadoDisponibilidad) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.especialidad = especialidad;
        this.turno = turno;
        this.estadoDisponibilidad = estadoDisponibilidad;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getEstadoDisponibilidad() {
        return estadoDisponibilidad;
    }

    public void setEstadoDisponibilidad(String estadoDisponibilidad) {
        this.estadoDisponibilidad = estadoDisponibilidad;
    }

    @Override
    public String toString() {
        return "TecnicosDTO{" + "id=" + id + ", " + "idUsuario=" + idUsuario + ", " + "especialidad=" + especialidad + ", " + "turno=" + turno + ", " + "estadoDisponibilidad=" + estadoDisponibilidad + "}";
    }
}
