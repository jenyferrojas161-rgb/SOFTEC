package com.softech.softech.notificaciones;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private final Map<Long, Set<SseEmitter>> canales = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbc;

    public NotificationService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SseEmitter conectar(Long idUsuario) {
        SseEmitter emitter = new SseEmitter(0L);
        canales.computeIfAbsent(idUsuario, k -> ConcurrentHashMap.newKeySet()).add(emitter);
        emitter.onCompletion(() -> remover(idUsuario, emitter));
        emitter.onTimeout(() -> remover(idUsuario, emitter));
        emitter.onError(e -> remover(idUsuario, emitter));
        try {
            emitter.send(SseEmitter.event().name("conexion").data(Map.of("ok", true)));
        } catch (IOException e) {
            remover(idUsuario, emitter);
        }
        return emitter;
    }

    /** Guarda la notificación en la base de datos (para que la vea al entrar) y la envía en vivo si está conectado. */
    public void notificar(Long idUsuario, String tipo, String mensaje) {
        if (idUsuario == null) return;
        if (!notificacionesActivas()) return;

        Long idNotificacion = null;
        try {
            idNotificacion = jdbc.queryForObject(
                    "INSERT INTO notificaciones (id_usuario, tipo, mensaje) VALUES (?, ?, ?) RETURNING id_notificacion",
                    Long.class, idUsuario, tipo, mensaje);
        } catch (Exception e) {
            // Si no se puede guardar, igual se intenta el aviso en vivo.
            org.slf4j.LoggerFactory.getLogger(NotificationService.class)
                    .error("No se pudo guardar la notificación para el usuario {}", idUsuario, e);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", idNotificacion);
        payload.put("tipo", tipo);
        payload.put("mensaje", mensaje);

        Set<SseEmitter> emisores = canales.getOrDefault(idUsuario, Set.of());
        for (SseEmitter emitter : emisores) {
            try {
                emitter.send(SseEmitter.event().name("notificacion").data(payload));
            } catch (IOException e) {
                remover(idUsuario, emitter);
            }
        }
    }

    /** Respeta el ajuste \"Notificaciones automáticas\" de Configuración. */
    private boolean notificacionesActivas() {
        try {
            List<String> v = jdbc.queryForList("SELECT valor FROM configuracion WHERE clave = 'notificaciones'", String.class);
            return v.isEmpty() || !"Desactivadas".equals(v.get(0));
        } catch (Exception e) {
            return true;
        }
    }

    public List<Map<String, Object>> listarDe(Long idUsuario) {
        return jdbc.queryForList("""
                SELECT id_notificacion AS "id", tipo AS "tipo", mensaje AS "mensaje",
                       leida AS "leida", fecha_hora AS "fechaHora"
                FROM notificaciones
                WHERE id_usuario = ?
                ORDER BY fecha_hora DESC, id_notificacion DESC
                LIMIT 30
                """, idUsuario);
    }

    public int marcarTodasLeidas(Long idUsuario) {
        return jdbc.update("UPDATE notificaciones SET leida = TRUE WHERE id_usuario = ? AND leida = FALSE", idUsuario);
    }

    private void remover(Long idUsuario, SseEmitter emitter) {
        Set<SseEmitter> set = canales.get(idUsuario);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) canales.remove(idUsuario);
        }
    }
}
