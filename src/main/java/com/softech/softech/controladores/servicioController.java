package com.softech.softech.controladores;

import com.softech.softech.DTOs.ServiciosDTO;
import com.softech.softech.DTOs.SolicitudServicioRequest;
import com.softech.softech.Servicios.ServicioServicio;
import com.softech.softech.Servicios.SolicitudServicioServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
public class servicioController {

    @Autowired private ServicioServicio servicio;
    @Autowired private SolicitudServicioServicio solicitudServicio;

    @GetMapping
    public List<ServiciosDTO> listar() { return servicio.listar(); }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @AuditAction("CREAR_SERVICIO")
    public ServiciosDTO insertar(@RequestBody ServiciosDTO dto) { return servicio.insertar(dto); }

    /** Crea cliente (si es nuevo) + equipo + servicio (+ técnico) a partir de datos escritos en el formulario. */
    @PostMapping("/solicitud")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @AuditAction("CREAR_SOLICITUD_SERVICIO")
    public Map<String, Object> solicitud(@RequestBody SolicitudServicioRequest req) { return solicitudServicio.crear(req); }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @AuditAction("MODIFICAR_SERVICIO")
    public ServiciosDTO actualizar(@PathVariable Long id, @RequestBody ServiciosDTO dto) { return servicio.actualizar(id, dto); }

    @PutMapping("/{id}/estado")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','TECNICO')")
    @AuditAction("MODIFICAR_ESTADO_MANTENIMIENTO")
    public ServiciosDTO actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null || estado.isBlank()) throw new IllegalArgumentException("El estado es obligatorio.");
        return servicio.actualizarEstado(id, estado);
    }

    /** Avance del técnico: estado + diagnóstico + nota. Queda en el historial del servicio. */
    @PutMapping("/{id}/seguimiento")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','TECNICO')")
    @AuditAction("REGISTRAR_SEGUIMIENTO")
    public ServiciosDTO seguimiento(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null || estado.isBlank()) throw new IllegalArgumentException("El estado es obligatorio.");
        return servicio.seguimiento(id, estado, body.get("diagnostico"), body.get("nota"));
    }

    @GetMapping("/{id}/historial")
    public List<Map<String, Object>> historial(@PathVariable Long id) { return servicio.historial(id); }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @AuditAction("ELIMINAR_SERVICIO")
    public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
