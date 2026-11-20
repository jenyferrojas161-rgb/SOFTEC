package com.softech.softech.controladores;

import com.softech.softech.DTOs.UsuarioDTO;
import com.softech.softech.Servicios.UsuarioServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class usuarioController {
    @Autowired private UsuarioServicio servicio;

    @GetMapping public List<UsuarioDTO> listar() { return servicio.listar(); }

    @PostMapping
    @AuditAction("CREAR_USUARIO")
    public UsuarioDTO insertar(@RequestBody UsuarioDTO dto) { return servicio.insertar(dto); }

    @PutMapping("/{id}")
    @AuditAction("MODIFICAR_USUARIO")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) { return servicio.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    @AuditAction("ELIMINAR_USUARIO")
    public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
