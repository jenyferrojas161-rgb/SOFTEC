package com.softech.softech.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Size(max = 150, message = "El usuario es demasiado largo.") String usuario,
        @Size(max = 150, message = "El correo es demasiado largo.") String email,
        @NotBlank(message = "Ingresa tu contraseña.") @Size(max = 128, message = "La contraseña es demasiado larga.") String contrasena) {
    public String identificador() {
        if (usuario != null && !usuario.isBlank()) return usuario.trim();
        return email == null ? "" : email.trim();
    }
}
