package com.softech.softech.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Recuperación de contraseña: además del usuario/correo se exige verificar la identidad. */
public record ResetPasswordRequest(
        @NotBlank(message = "Ingresa tu usuario o correo.") String identificador,
        @NotBlank(message = "Ingresa tu número de identificación.") String identificacion,
        String telefono,
        @NotBlank(message = "Ingresa la nueva contraseña.") @Size(max = 128, message = "La contraseña es demasiado larga.") String nuevaContrasena) {}
