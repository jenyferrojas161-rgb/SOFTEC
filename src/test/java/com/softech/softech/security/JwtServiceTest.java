package com.softech.softech.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // Secreto de prueba (Base64, más de 256 bits). No es el de ningún entorno real.
    private static final String SECRETO_PRUEBA = "VGVzdFNlY3JldG9QYXJhUHJ1ZWJhc1VuaXRhcmlhc1NvZnRlYzEyMzQ1Njc4";

    private JwtService servicio() {
        return new JwtService(SECRETO_PRUEBA, 60);
    }

    @Test
    void generaTokenQueSePuedeLeerDeNuevo() {
        JwtService jwt = servicio();
        String token = jwt.generar(7L, "Administrador", "Andrea");
        assertEquals(7L, jwt.obtenerUsuarioId(token));
        assertEquals("ADMIN", jwt.obtenerRol(token));
    }

    @Test
    void normalizaLosRoles() {
        JwtService jwt = servicio();
        assertEquals("TECNICO", jwt.obtenerRol(jwt.generar(1L, "Técnico", "Luis")));
        assertEquals("CLIENTE", jwt.obtenerRol(jwt.generar(2L, "Cliente", "Marta")));
        assertEquals("RECEPCIONISTA", jwt.obtenerRol(jwt.generar(3L, "Recepcionista", "Sara")));
    }

    @Test
    void rechazaTokenAlterado() {
        JwtService jwt = servicio();
        String token = jwt.generar(7L, "Cliente", "Andrea");
        int medio = token.length() / 2;
        char original = token.charAt(medio);
        String alterado = token.substring(0, medio) + (original == 'A' ? 'B' : 'A') + token.substring(medio + 1);
        assertThrows(JwtException.class, () -> jwt.validar(alterado));
    }

    @Test
    void rechazaTokenFirmadoConOtroSecreto() {
        String token = new JwtService("T3RyYVNlY3JldG9EaXN0aW50b0FsRGVMYXNQcnVlYmFzMTIzNDU2Nzg5MA==", 60)
                .generar(7L, "Cliente", "Andrea");
        assertThrows(JwtException.class, () -> servicio().validar(token));
    }

    @Test
    void rechazaTokenVencido() {
        JwtService jwt = new JwtService(SECRETO_PRUEBA, -1);
        String token = jwt.generar(7L, "Cliente", "Andrea");
        assertThrows(ExpiredJwtException.class, () -> jwt.validar(token));
    }

    @Test
    void noArrancaConSecretoCorto() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("Y29ydG8=", 60));
    }
}
