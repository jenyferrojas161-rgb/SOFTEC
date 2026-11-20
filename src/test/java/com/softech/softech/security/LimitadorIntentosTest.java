package com.softech.softech.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimitadorIntentosTest {

    @Test
    void noBloqueaSiNoHayFallos() {
        assertFalse(new LimitadorIntentos().bloqueado("login:ana@correo.com", 5));
    }

    @Test
    void bloqueaAlLlegarAlMaximoDeFallos() {
        LimitadorIntentos limitador = new LimitadorIntentos();
        for (int i = 0; i < 4; i++) limitador.registrarFallo("login:ana");
        assertFalse(limitador.bloqueado("login:ana", 5), "con 4 fallos todavía no debe bloquear");
        limitador.registrarFallo("login:ana");
        assertTrue(limitador.bloqueado("login:ana", 5), "con 5 fallos debe bloquear");
    }

    @Test
    void cadaClaveSeContabilizaPorSeparado() {
        LimitadorIntentos limitador = new LimitadorIntentos();
        for (int i = 0; i < 5; i++) limitador.registrarFallo("login:ana");
        assertTrue(limitador.bloqueado("login:ana", 5));
        assertFalse(limitador.bloqueado("login:luis", 5));
    }

    @Test
    void limpiarQuitaElBloqueo() {
        LimitadorIntentos limitador = new LimitadorIntentos();
        for (int i = 0; i < 5; i++) limitador.registrarFallo("login:ana");
        limitador.limpiar("login:ana");
        assertFalse(limitador.bloqueado("login:ana", 5));
    }
}
