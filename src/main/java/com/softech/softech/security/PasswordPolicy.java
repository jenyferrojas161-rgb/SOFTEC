package com.softech.softech.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {
    public void validar(String password) {
        if (password == null || password.length() < 10) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 10 caracteres.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una mayúscula.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una minúscula.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un número.");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un carácter especial.");
        }
    }
}
