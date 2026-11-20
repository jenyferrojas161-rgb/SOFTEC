package com.softech.softech.repositorio;

import com.softech.softech.modelo.usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface reposi_usuario extends JpaRepository<usuario, Long> {

    Optional<usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIdentificacion(String identificacion);
    Optional<usuario> findByIdentificacion(String identificacion);

    @Query("select u from usuario u where lower(u.email) = lower(:valor) or lower(u.nombreUsuario) = lower(:valor) or u.identificacion = :valor")
    Optional<usuario> buscarPorIdentificador(@Param("valor") String valor);
}
