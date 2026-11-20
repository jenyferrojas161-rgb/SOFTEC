package com.softech.softech.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class detallesFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalles_facturacion")
    private Long id;

    @Column(name = "id_facturacion")
    private Long idFacturacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "total")
    private Double total;
}
