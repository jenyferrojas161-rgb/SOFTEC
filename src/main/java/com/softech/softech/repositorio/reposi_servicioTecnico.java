package com.softech.softech.repositorio;

import com.softech.softech.modelo.servicioTecnico;
import com.softech.softech.modelo.ServicioTecnicoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface reposi_servicioTecnico extends JpaRepository<servicioTecnico, ServicioTecnicoId> {
}
