package com.softech.softech.configuracion;

import com.softech.softech.auditoria.AuditAction;
import com.softech.softech.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConfiguracionServicio servicio;

    public ConfiguracionController(ConfiguracionServicio servicio) {
        this.servicio = servicio;
    }

    /** Pública: la usan el login, el menú y el chatbot para mostrar nombre, logo y modo. */
    @GetMapping("/publica")
    public Map<String, String> publica() {
        return servicio.obtener();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("MODIFICAR_CONFIGURACION")
    public Map<String, String> guardar(@RequestBody Map<String, String> body) {
        return servicio.guardar(body, CurrentUser.id());
    }
}
