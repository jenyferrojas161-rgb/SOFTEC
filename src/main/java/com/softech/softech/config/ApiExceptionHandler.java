package com.softech.softech.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() == null ? "Datos no válidos." : e.getMessage()));
    }

    /* Datos de entrada no válidos (@Valid en los DTOs): responde 400 con el primer mensaje legible. */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .filter(m -> m != null && !m.isBlank())
                .findFirst()
                .orElse("Los datos enviados no son válidos. Revisa el formulario.");
        return ResponseEntity.badRequest().body(Map.of("message", mensaje));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoIlegible(org.springframework.http.converter.HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "No se pudo leer la solicitud. Revisa los datos enviados."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> conflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage() == null ? "La operación no está permitida." : e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> forbidden(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "No tienes permiso para realizar esta acción."));
    }

    /* Errores de la base de datos (llave duplicada, llave foránea, dato obligatorio, CHECK).
       Antes caían en el 500 genérico y no quedaba rastro en la consola. */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(org.springframework.dao.DataIntegrityViolationException e) {
        Throwable causa = e.getMostSpecificCause();
        log.warn("Violación de integridad en la base de datos: {}", causa.getMessage());
        String estado = causa instanceof java.sql.SQLException sql ? sql.getSQLState() : "";
        String mensaje = switch (estado == null ? "" : estado) {
            case "23505" -> "Ya existe un registro con esos datos.";
            case "23503" -> "No se puede completar la operación porque el registro está relacionado con otros datos.";
            case "23502" -> "Faltan datos obligatorios. Revisa el formulario.";
            case "23514" -> "Alguno de los valores enviados no es válido.";
            default -> "Los datos enviados entran en conflicto con las reglas de la base de datos.";
        };
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> internal(Exception e) {
        log.error("Error no controlado en la API", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "El servidor no pudo completar la operación."));
    }
}
