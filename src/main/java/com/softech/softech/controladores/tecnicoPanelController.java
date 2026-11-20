package com.softech.softech.controladores;

import com.softech.softech.auditoria.AuditAction;
import com.softech.softech.security.CurrentUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Endpoints del propio técnico (su ficha y su disponibilidad). */
@RestController
@RequestMapping("/api/tecnicos/me")
public class tecnicoPanelController {

    private static final Set<String> DISPONIBILIDAD = Set.of("Disponible", "Ocupado", "Inactivo");

    private final JdbcTemplate jdbc;

    public tecnicoPanelController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Map<String, Object> ficha() {
        List<Map<String, Object>> filas = jdbc.queryForList("""
                SELECT t.id_tecnico AS "id", t.id_usuario AS "idUsuario", t.especialidad AS "especialidad",
                       t.turno AS "turno", t.estado_disponibilidad AS "estadoDisponibilidad"
                FROM tecnicos t WHERE t.id_usuario = ?
                """, CurrentUser.id());
        if (filas.isEmpty()) throw new IllegalArgumentException("Tu cuenta todavía no tiene una ficha de técnico. Pídele a la administración que la cree.");
        return filas.get(0);
    }

    @GetMapping
    @PreAuthorize("hasRole('TECNICO')")
    public Map<String, Object> me() {
        return ficha();
    }

    @PutMapping("/disponibilidad")
    @PreAuthorize("hasRole('TECNICO')")
    @AuditAction("CAMBIAR_DISPONIBILIDAD")
    public Map<String, Object> disponibilidad(@RequestBody Map<String, String> body) {
        String estado = body.get("estadoDisponibilidad");
        if (estado == null || !DISPONIBILIDAD.contains(estado)) throw new IllegalArgumentException("Disponibilidad no válida.");
        Map<String, Object> actual = ficha();
        jdbc.update("UPDATE tecnicos SET estado_disponibilidad = ? WHERE id_tecnico = ?", estado, ((Number) actual.get("id")).longValue());
        return ficha();
    }
}
