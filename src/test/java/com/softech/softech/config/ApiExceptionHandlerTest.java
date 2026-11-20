package com.softech.softech.config;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void datosNoValidosDevuelve400ConElMensaje() {
        ResponseEntity<Map<String, Object>> r = handler.badRequest(new IllegalArgumentException("Dato inválido"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Dato inválido", r.getBody().get("message"));
    }

    @Test
    void operacionNoPermitidaDevuelve409() {
        ResponseEntity<Map<String, Object>> r = handler.conflict(new IllegalStateException("La factura ya fue procesada y es inmutable."));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
    }

    @Test
    void registroDuplicadoSeTraduceAUnMensajeClaro() {
        var e = new DataIntegrityViolationException("x", new SQLException("duplicate key", "23505"));
        ResponseEntity<Map<String, Object>> r = handler.integridad(e);
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals("Ya existe un registro con esos datos.", r.getBody().get("message"));
    }

    @Test
    void llaveForaneaSeTraduceAUnMensajeClaro() {
        var e = new DataIntegrityViolationException("x", new SQLException("fk", "23503"));
        assertTrue(handler.integridad(e).getBody().get("message").toString().contains("relacionado"));
    }

    @Test
    void errorInesperadoNoExponeDetallesTecnicos() {
        ResponseEntity<Map<String, Object>> r = handler.internal(new RuntimeException("NullPointerException en línea 42"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertFalse(r.getBody().get("message").toString().contains("NullPointer"));
    }
}
