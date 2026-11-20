package com.softech.softech.controladores;

import com.softech.softech.DTOs.EquipoDTO;
import com.softech.softech.Servicios.EquipoServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class equipoController {
    @Autowired private EquipoServicio servicio;
    @GetMapping public List<EquipoDTO> listar() { return servicio.listar(); }
    @PostMapping @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @AuditAction("CREAR_EQUIPO") public EquipoDTO insertar(@RequestBody EquipoDTO dto) { return servicio.insertar(dto); }
    @PutMapping("/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @AuditAction("MODIFICAR_EQUIPO") public EquipoDTO actualizar(@PathVariable Long id, @RequestBody EquipoDTO dto) { return servicio.actualizar(id, dto); }
    @DeleteMapping("/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @AuditAction("ELIMINAR_EQUIPO") public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
