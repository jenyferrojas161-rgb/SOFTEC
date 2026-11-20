package com.softech.softech.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "La identificación es obligatoria.") @Size(max = 20, message = "La identificación es demasiado larga.") String identificacion,
        @NotBlank(message = "El nombre es obligatorio.") @Size(max = 100, message = "El nombre es demasiado largo.") String nombreUsuario,
        @Size(max = 20, message = "El teléfono es demasiado largo.") String telefono,
        @NotBlank(message = "El correo es obligatorio.") @Email(message = "El correo no tiene un formato válido.") @Size(max = 150, message = "El correo es demasiado largo.") String email,
        @NotBlank(message = "La contraseña es obligatoria.") @Size(max = 128, message = "La contraseña es demasiado larga.") String contrasena,
        String rol,
        String codigoInvitacion
) {}
