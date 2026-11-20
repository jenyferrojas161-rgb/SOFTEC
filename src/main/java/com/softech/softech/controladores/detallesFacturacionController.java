package com.softech.softech.controladores;

import com.softech.softech.DTOs.DetallesFacturacionDTO;
import com.softech.softech.Servicios.DetallesFacturacionServicio;
import com.softech.softech.auditoria.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-facturacion")
@PreAuthorize("hasRole('ADMIN')")
public class detallesFacturacionController {
    @Autowired private DetallesFacturacionServicio servicio;
    @GetMapping public List<DetallesFacturacionDTO> listar() { return servicio.listar(); }
    @PostMapping @AuditAction("CREAR_DETALLE_FACTURA") public DetallesFacturacionDTO insertar(@RequestBody DetallesFacturacionDTO dto) { return servicio.insertar(dto); }
    @PutMapping("/{id}") @AuditAction("MODIFICAR_DETALLE_FACTURA") public DetallesFacturacionDTO actualizar(@PathVariable Long id, @RequestBody DetallesFacturacionDTO dto) { return servicio.actualizar(id, dto); }
    @DeleteMapping("/{id}") @AuditAction("ELIMINAR_DETALLE_FACTURA") public String eliminar(@PathVariable Long id) { return servicio.eliminar(id); }
}
