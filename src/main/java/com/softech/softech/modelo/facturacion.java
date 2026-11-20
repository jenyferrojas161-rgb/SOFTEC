package com.softech.softech.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long id;

    @Column(name = "id_servicio")
    private Long idServicio;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "numero_factura")
    private String numeroFactura;

    @Column(name = "fecha_factura")
    private LocalDate fechaFactura;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "tipo_comprobante")
    private String tipoComprobante;

    @Column(name = "total_pagar")
    private Double totalPagar;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "estado_pago")
    private String estadoPago;
}
