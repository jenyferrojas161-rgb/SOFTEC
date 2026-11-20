package com.softech.softech.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "servicio_tecnico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class servicioTecnico {

    @EmbeddedId
    private ServicioTecnicoId id;

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion;
}
