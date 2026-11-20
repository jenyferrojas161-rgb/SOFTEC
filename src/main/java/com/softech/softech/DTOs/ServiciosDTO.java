package com.softech.softech.DTOs;
import java.time.LocalDate;

public class ServiciosDTO {
    private Long id;
    private Long idEquipo;
    private Long idUsuario;
    private LocalDate fechaIngresoEquipo;
    private LocalDate fechaEntregaEquipo;
    private String problemaReportado;
    private String diagnosticoRealizado;
    private String estadoServicio;
    private String descripcionProblema;
    private String prioridad;
    private String tipoServicio;

    public ServiciosDTO(Long id, Long idEquipo, Long idUsuario, LocalDate fechaIngresoEquipo, LocalDate fechaEntregaEquipo, String problemaReportado, String diagnosticoRealizado, String estadoServicio, String descripcionProblema, String prioridad, String tipoServicio) {
        this.id = id;
        this.idEquipo = idEquipo;
        this.idUsuario = idUsuario;
        this.fechaIngresoEquipo = fechaIngresoEquipo;
        this.fechaEntregaEquipo = fechaEntregaEquipo;
        this.problemaReportado = problemaReportado;
        this.diagnosticoRealizado = diagnosticoRealizado;
        this.estadoServicio = estadoServicio;
        this.descripcionProblema = descripcionProblema;
        this.prioridad = prioridad;
        this.tipoServicio = tipoServicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getFechaIngresoEquipo() {
        return fechaIngresoEquipo;
    }

    public void setFechaIngresoEquipo(LocalDate fechaIngresoEquipo) {
        this.fechaIngresoEquipo = fechaIngresoEquipo;
    }

    public LocalDate getFechaEntregaEquipo() {
        return fechaEntregaEquipo;
    }

    public void setFechaEntregaEquipo(LocalDate fechaEntregaEquipo) {
        this.fechaEntregaEquipo = fechaEntregaEquipo;
    }

    public String getProblemaReportado() {
        return problemaReportado;
    }

    public void setProblemaReportado(String problemaReportado) {
        this.problemaReportado = problemaReportado;
    }

    public String getDiagnosticoRealizado() {
        return diagnosticoRealizado;
    }

    public void setDiagnosticoRealizado(String diagnosticoRealizado) {
        this.diagnosticoRealizado = diagnosticoRealizado;
    }

    public String getEstadoServicio() {
        return estadoServicio;
    }

    public void setEstadoServicio(String estadoServicio) {
        this.estadoServicio = estadoServicio;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    @Override
    public String toString() {
        return "ServiciosDTO{" + "id=" + id + ", " + "idEquipo=" + idEquipo + ", " + "idUsuario=" + idUsuario + ", " + "fechaIngresoEquipo=" + fechaIngresoEquipo + ", " + "fechaEntregaEquipo=" + fechaEntregaEquipo + ", " + "problemaReportado=" + problemaReportado + ", " + "diagnosticoRealizado=" + diagnosticoRealizado + ", " + "estadoServicio=" + estadoServicio + ", " + "descripcionProblema=" + descripcionProblema + ", " + "prioridad=" + prioridad + ", " + "tipoServicio=" + tipoServicio + "}";
    }
}
