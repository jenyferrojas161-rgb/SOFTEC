package com.softech.softech.DTOs;

public class DetallesFacturacionDTO {
    private Long id;
    private Long idFacturacion;
    private String descripcion;
    private Integer cantidad;
    private Double precio;
    private Double total;

    public DetallesFacturacionDTO(Long id, Long idFacturacion, String descripcion, Integer cantidad, Double precio, Double total) {
        this.id = id;
        this.idFacturacion = idFacturacion;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFacturacion() {
        return idFacturacion;
    }

    public void setIdFacturacion(Long idFacturacion) {
        this.idFacturacion = idFacturacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "DetallesFacturacionDTO{" + "id=" + id + ", " + "idFacturacion=" + idFacturacion + ", " + "descripcion=" + descripcion + ", " + "cantidad=" + cantidad + ", " + "precio=" + precio + ", " + "total=" + total + "}";
    }
}
