package com.softech.softech.controladores;

import com.softech.softech.DTOs.ClientesDTO;
import com.softech.softech.Servicios.ClienteServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class clienteController {
    @Autowired private ClienteServicio servicio;
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECNICO','CLIENTE')")
    public List<ClientesDTO> listar() { return servicio.listar(); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") @AuditAction("CREAR_CLIENTE") public ClientesDTO insertar(@RequestBody ClientesDTO dto) { return servicio.insertar(dto); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @AuditAction("MODIFICAR_CLIENTE") public ClientesDTO actualizar(@PathVariable Long id, @RequestBody ClientesDTO dto) { return servicio.actualizar(id, dto); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @AuditAction("ELIMINAR_CLIENTE") public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
