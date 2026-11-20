package com.softech.softech.controladores;

import com.softech.softech.DTOs.EstadoServicioDTO;
import com.softech.softech.DTOs.ServicioTecnicoDTO;
import com.softech.softech.Servicios.ServicioTecnicoServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicio-tecnico")
public class servicioTecnicoController {
    private static final int MAX_CONSULTAS_PUBLICAS = 30; // por IP cada 15 minutos
    @Autowired private ServicioTecnicoServicio servicio;
    @Autowired private com.softech.softech.security.LimitadorIntentos limitador;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','TECNICO')") public List<ServicioTecnicoDTO> listar() { return servicio.listar(); }
    @GetMapping("/buscar") public ResponseEntity<?> buscar(@RequestParam(required=false) String orden, @RequestParam(required=false) String cedula,
                                          jakarta.servlet.http.HttpServletRequest http) {
        // Consulta pública: se limita por IP para evitar que alguien recorra números de orden o cédulas.
        String clave = "buscar:" + http.getRemoteAddr();
        if (limitador.bloqueado(clave, MAX_CONSULTAS_PUBLICAS)) {
            return ResponseEntity.status(429).body(java.util.Map.of("message", "Demasiadas consultas seguidas. Intenta de nuevo en unos minutos."));
        }
        limitador.registrarFallo(clave);
        if ((orden == null || orden.isBlank()) && (cedula == null || cedula.isBlank())) return ResponseEntity.badRequest().body(java.util.Map.of("message", "Ingresa un número de orden o una cédula."));
        EstadoServicioDTO estado = servicio.buscarEstado(orden, cedula);
        if (estado == null) return ResponseEntity.status(404).body(java.util.Map.of("message", "No encontramos un servicio con esos datos."));
        return ResponseEntity.ok(estado);
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("ASIGNAR_TECNICO")
    public ServicioTecnicoDTO insertar(@RequestBody ServicioTecnicoDTO dto) { return servicio.insertar(dto); }
    @PutMapping("/{idServicio}/{idTecnico}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("MODIFICAR_ASIGNACION")
    public ServicioTecnicoDTO actualizar(@PathVariable Long idServicio, @PathVariable Long idTecnico, @RequestBody ServicioTecnicoDTO dto) { return servicio.actualizar(idServicio, idTecnico, dto); }
    @DeleteMapping("/{idServicio}/{idTecnico}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("ELIMINAR_ASIGNACION")
    public String eliminar(@PathVariable Long idServicio, @PathVariable Long idTecnico) { return servicio.eliminar(idServicio, idTecnico); }
}
