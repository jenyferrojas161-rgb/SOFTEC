package com.softech.softech.controladores;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class auditoriaController {

    private final JdbcTemplate jdbc;

    public auditoriaController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Actividad registrada (quién hizo qué y cuándo). Con idUsuario devuelve solo la de esa persona. */
    @GetMapping
    public List<Map<String, Object>> listar(@RequestParam(required = false) Long idUsuario,
                                            @RequestParam(defaultValue = "60") int limite) {
        int tope = Math.max(1, Math.min(limite, 200));
        String base = """
                SELECT a.id_audit AS "id", a.id_usuario AS "idUsuario", u.nombre_usuario AS "usuario",
                       u.tipo_usuario AS "rol", a.accion AS "accion", a.metodo_http AS "metodo",
                       a.ruta AS "ruta", a.fecha_hora AS "fechaHora"
                FROM audit_logs a
                LEFT JOIN usuario u ON u.id_usuario = a.id_usuario
                """;
        if (idUsuario != null) {
            return jdbc.queryForList(base + " WHERE a.id_usuario = ? ORDER BY a.fecha_hora DESC, a.id_audit DESC LIMIT ?", idUsuario, tope);
        }
        return jdbc.queryForList(base + " ORDER BY a.fecha_hora DESC, a.id_audit DESC LIMIT ?", tope);
    }
}
