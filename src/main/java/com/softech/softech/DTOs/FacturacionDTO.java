package com.softech.softech.DTOs;
import java.time.LocalDate;

public class FacturacionDTO {
    private Long id;
    private Long idServicio;
    private Long idCliente;
    private String numeroFactura;
    private LocalDate fechaFactura;
    private LocalDate fechaPago;
    private String tipoComprobante;
    private Double totalPagar;
    private String metodoPago;
    private String estadoPago;

    public FacturacionDTO(Long id, Long idServicio, Long idCliente, String numeroFactura, LocalDate fechaFactura, LocalDate fechaPago, String tipoComprobante, Double totalPagar, String metodoPago, String estadoPago) {
        this.id = id;
        this.idServicio = idServicio;
        this.idCliente = idCliente;
        this.numeroFactura = numeroFactura;
        this.fechaFactura = fechaFactura;
        this.fechaPago = fechaPago;
        this.tipoComprobante = tipoComprobante;
        this.totalPagar = totalPagar;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDate getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(LocalDate fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(Double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    @Override
    public String toString() {
        return "FacturacionDTO{" + "id=" + id + ", " + "idServicio=" + idServicio + ", " + "idCliente=" + idCliente + ", " + "numeroFactura=" + numeroFactura + ", " + "fechaFactura=" + fechaFactura + ", " + "fechaPago=" + fechaPago + ", " + "tipoComprobante=" + tipoComprobante + ", " + "totalPagar=" + totalPagar + ", " + "metodoPago=" + metodoPago + ", " + "estadoPago=" + estadoPago + "}";
    }
}
