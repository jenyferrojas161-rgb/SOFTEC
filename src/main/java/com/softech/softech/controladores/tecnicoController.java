package com.softech.softech.controladores;

import com.softech.softech.DTOs.TecnicosDTO;
import com.softech.softech.Servicios.TecnicoServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
@PreAuthorize("hasRole('ADMIN')")
public class tecnicoController {
    @Autowired private TecnicoServicio servicio;
    @GetMapping public List<TecnicosDTO> listar() { return servicio.listar(); }
    @PostMapping @AuditAction("CREAR_TECNICO") public TecnicosDTO insertar(@RequestBody TecnicosDTO dto) { return servicio.insertar(dto); }
    @PutMapping("/{id}") @AuditAction("MODIFICAR_TECNICO") public TecnicosDTO actualizar(@PathVariable Long id, @RequestBody TecnicosDTO dto) { return servicio.actualizar(id, dto); }
    @DeleteMapping("/{id}") @AuditAction("ELIMINAR_TECNICO") public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
