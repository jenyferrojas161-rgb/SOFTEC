package com.softech.softech.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tecnicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class tecnicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico")
    private Long id;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "turno")
    private String turno;

    @Column(name = "estado_disponibilidad")
    private String estadoDisponibilidad;
}
