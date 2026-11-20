package com.softech.softech.repositorio;

import com.softech.softech.modelo.clientes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface reposi_clientes extends JpaRepository<clientes, Long> {
    java.util.Optional<clientes> findByIdUsuario(Long idUsuario);
}
