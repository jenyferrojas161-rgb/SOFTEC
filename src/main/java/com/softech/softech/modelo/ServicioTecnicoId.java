package com.softech.softech.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// Clave primaria compuesta de servicio_tecnico (id_servicio + id_tecnico)
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioTecnicoId implements Serializable {

    @Column(name = "id_servicio")
    private Long idServicio;

    @Column(name = "id_tecnico")
    private Long idTecnico;
}
