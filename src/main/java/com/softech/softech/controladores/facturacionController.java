package com.softech.softech.controladores;

import com.softech.softech.DTOs.FacturacionDTO;
import com.softech.softech.Servicios.FacturacionServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.softech.softech.security.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/facturacion")
public class facturacionController {

    @Autowired private FacturacionServicio servicio;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FacturacionDTO> listar() { return servicio.listar(); }

    @GetMapping("/mis-facturas")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<FacturacionDTO> misFacturas() { return servicio.listarDelCliente(CurrentUser.id()); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("EMITIR_FACTURA")
    public FacturacionDTO insertar(@RequestBody FacturacionDTO dto) { return servicio.insertar(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("MODIFICAR_FACTURA")
    public FacturacionDTO actualizar(@PathVariable Long id, @RequestBody FacturacionDTO dto) { return servicio.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditAction("ELIMINAR_FACTURA")
    public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
