package com.softech.softech.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class servicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long id;

    @Column(name = "id_equipo")
    private Long idEquipo;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "fecha_ingreso_equipo")
    private LocalDate fechaIngresoEquipo;

    @Column(name = "fecha_entrega_equipo")
    private LocalDate fechaEntregaEquipo;

    @Column(name = "problema_reportado")
    private String problemaReportado;

    @Column(name = "diagnostico_realizado")
    private String diagnosticoRealizado;

    @Column(name = "estado_servicio")
    private String estadoServicio;

    @Column(name = "descripcion_problema")
    private String descripcionProblema;

    @Column(name = "prioridad")
    private String prioridad;

    @Column(name = "tipo_servicio")
    private String tipoServicio;
}
