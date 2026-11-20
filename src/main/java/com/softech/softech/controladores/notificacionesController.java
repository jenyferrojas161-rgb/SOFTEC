package com.softech.softech.controladores;

import com.softech.softech.notificaciones.NotificationService;
import com.softech.softech.security.CurrentUser;
import com.softech.softech.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class notificacionesController {
    private final NotificationService notifications;
    private final JwtService jwtService;

    public notificacionesController(NotificationService notifications, JwtService jwtService) {
        this.notifications = notifications;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> stream(@RequestParam String token) {
        Long id;
        try {
            id = jwtService.obtenerUsuarioId(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notifications.conectar(id));
    }

    /** Notificaciones guardadas del usuario autenticado (las 30 más recientes). */
    @GetMapping("/mias")
    public ResponseEntity<List<Map<String, Object>>> mias() {
        Long id = CurrentUser.id();
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(notifications.listarDe(id));
    }

    /** Marca como leídas todas las notificaciones del usuario autenticado. */
    @PutMapping("/mias/leidas")
    public ResponseEntity<Map<String, Object>> marcarLeidas() {
        Long id = CurrentUser.id();
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(Map.of("actualizadas", notifications.marcarTodasLeidas(id)));
    }
}
