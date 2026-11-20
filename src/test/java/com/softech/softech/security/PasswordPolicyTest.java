package com.softech.softech.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    private final PasswordPolicy politica = new PasswordPolicy();

    @Test
    void aceptaContrasenaQueCumpleTodasLasReglas() {
        assertDoesNotThrow(() -> politica.validar("Softec2026*ok"));
    }

    @Test
    void rechazaContrasenaNula() {
        assertThrows(IllegalArgumentException.class, () -> politica.validar(null));
    }

    @Test
    void rechazaContrasenaCorta() {
        var e = assertThrows(IllegalArgumentException.class, () -> politica.validar("Ab1*xyz"));
        assertTrue(e.getMessage().contains("10 caracteres"));
    }

    @Test
    void rechazaSinMayuscula() {
        var e = assertThrows(IllegalArgumentException.class, () -> politica.validar("softec2026*ok"));
        assertTrue(e.getMessage().contains("mayúscula"));
    }

    @Test
    void rechazaSinMinuscula() {
        var e = assertThrows(IllegalArgumentException.class, () -> politica.validar("SOFTEC2026*OK"));
        assertTrue(e.getMessage().contains("minúscula"));
    }

    @Test
    void rechazaSinNumero() {
        var e = assertThrows(IllegalArgumentException.class, () -> politica.validar("SoftecSegura*ok"));
        assertTrue(e.getMessage().contains("número"));
    }

    @Test
    void rechazaSinCaracterEspecial() {
        var e = assertThrows(IllegalArgumentException.class, () -> politica.validar("Softec2026ok99"));
        assertTrue(e.getMessage().contains("especial"));
    }
}
