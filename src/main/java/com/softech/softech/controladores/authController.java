package com.softech.softech.controladores;

import com.softech.softech.DTOs.AuthResponse;
import com.softech.softech.DTOs.LoginRequest;
import com.softech.softech.DTOs.RegistroRequest;
import com.softech.softech.DTOs.ResetPasswordRequest;
import com.softech.softech.Servicios.AuthServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class authController {

    private final AuthServicio servicio;

    public authController(AuthServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@jakarta.validation.Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = servicio.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@jakarta.validation.Valid @RequestBody RegistroRequest request) {
        try {
            servicio.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("message", "Cuenta creada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@jakarta.validation.Valid @RequestBody ResetPasswordRequest request,
                                           jakarta.servlet.http.HttpServletRequest http) {
        try {
            servicio.resetPassword(request, http.getRemoteAddr());
            return ResponseEntity.ok(java.util.Map.of("message", "Contraseña actualizada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
