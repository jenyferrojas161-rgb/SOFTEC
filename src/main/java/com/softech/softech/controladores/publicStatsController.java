package com.softech.softech.controladores;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/stats")
public class publicStatsController {
    private final JdbcTemplate jdbc;

    public publicStatsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public Map<String, Long> stats() {
        return Map.of(
                "clientes", contar("clientes"),
                "tecnicos", contar("tecnicos"),
                "servicios", contar("servicios"),
                "equipos", contar("equipo")
        );
    }

    private long contar(String tabla) {
        Long total = jdbc.queryForObject("select count(*) from " + tabla, Long.class);
        return total == null ? 0 : total;
    }
}
