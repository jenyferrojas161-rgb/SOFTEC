package com.softech.softech.configuracion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ConfiguracionServicio {

    private static final Set<String> CLAVES = Set.of("nombre", "correo", "dias", "notificaciones", "modo", "descripcion", "logo");

    private final JdbcTemplate jdbc;

    public ConfiguracionServicio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, String> obtener() {
        Map<String, String> r = new LinkedHashMap<>();
        jdbc.query("SELECT clave, valor FROM configuracion", (RowCallbackHandler) rs -> {
            r.put(rs.getString(1), rs.getString(2));
        });
        return r;
    }

    @Transactional
    public Map<String, String> guardar(Map<String, String> datos, Long idUsuario) {
        for (Map.Entry<String, String> e : datos.entrySet()) {
            String clave = e.getKey();
            if (!CLAVES.contains(clave)) continue;
            String valor = e.getValue() == null ? "" : e.getValue().trim();
            validar(clave, valor);
            jdbc.update("""
                    INSERT INTO configuracion (clave, valor, actualizado, actualizado_por)
                    VALUES (?, ?, CURRENT_TIMESTAMP, ?)
                    ON CONFLICT (clave) DO UPDATE
                    SET valor = EXCLUDED.valor, actualizado = CURRENT_TIMESTAMP, actualizado_por = EXCLUDED.actualizado_por
                    """, clave, valor, idUsuario == null ? null : idUsuario.intValue());
        }
        return obtener();
    }

    private void validar(String clave, String valor) {
        switch (clave) {
            case "nombre" -> {
                if (valor.isEmpty() || valor.length() > 60) throw new IllegalArgumentException("El nombre del software es obligatorio (máximo 60 caracteres).");
            }
            case "correo" -> {
                if (!valor.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("El correo de soporte no tiene un formato válido.");
            }
            case "dias" -> {
                int n;
                try { n = Integer.parseInt(valor); } catch (NumberFormatException ex) { throw new IllegalArgumentException("El tiempo máximo de reparación debe ser un número de días."); }
                if (n < 1 || n > 90) throw new IllegalArgumentException("El tiempo máximo de reparación debe estar entre 1 y 90 días.");
            }
            case "notificaciones" -> {
                if (!valor.equals("Activadas") && !valor.equals("Desactivadas")) throw new IllegalArgumentException("Valor de notificaciones no válido.");
            }
            case "modo" -> {
                if (!valor.equals("Claro") && !valor.equals("Oscuro")) throw new IllegalArgumentException("Valor de modo no válido.");
            }
            case "descripcion" -> {
                if (valor.length() > 500) throw new IllegalArgumentException("La descripción admite máximo 500 caracteres.");
            }
            case "logo" -> {
                if (!valor.isEmpty() && !valor.startsWith("data:image/")) throw new IllegalArgumentException("El logo debe ser una imagen.");
                if (valor.length() > 400_000) throw new IllegalArgumentException("El logo es demasiado pesado (máximo 250 KB).");
            }
            default -> { }
        }
    }
}
