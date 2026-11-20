package com.softech.softech.Servicios;

import com.softech.softech.DTOs.FacturacionDTO;
import com.softech.softech.Mapper.FacturacionMapper;
import com.softech.softech.modelo.facturacion;
import com.softech.softech.repositorio.reposi_facturacion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturacionServicio {

    @Autowired private reposi_facturacion repo;
    @Autowired private JdbcTemplate jdbc;

    public List<FacturacionDTO> listar() {
        return repo.findAll().stream().map(FacturacionMapper::toDTO).collect(Collectors.toList());
    }


    public List<FacturacionDTO> listarDelCliente(Long idUsuario) {
        List<Long> ids = jdbc.queryForList("""
                SELECT f.id_factura
                FROM facturacion f
                JOIN clientes c ON c.id_cliente = f.id_cliente
                WHERE c.id_usuario = ?
                ORDER BY f.fecha_factura DESC, f.id_factura DESC
                """, Long.class, idUsuario);
        return repo.findAllById(ids).stream().map(FacturacionMapper::toDTO).collect(Collectors.toList());
    }
    public FacturacionDTO insertar(FacturacionDTO dto) {
        facturacion entidad = FacturacionMapper.toEntity(dto);
        facturacion guardado = repo.save(entidad);
        return FacturacionMapper.toDTO(guardado);
    }

    @Transactional
    public FacturacionDTO actualizar(Long id, FacturacionDTO dto) {
        facturacion existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id " + id));
        bloquearSiProcesada(existente);

        existente.setIdServicio(dto.getIdServicio());
        existente.setIdCliente(dto.getIdCliente());
        existente.setNumeroFactura(dto.getNumeroFactura());
        existente.setFechaFactura(dto.getFechaFactura());
        existente.setFechaPago(dto.getFechaPago());
        existente.setTipoComprobante(dto.getTipoComprobante());
        existente.setTotalPagar(dto.getTotalPagar());
        existente.setMetodoPago(dto.getMetodoPago());
        existente.setEstadoPago(dto.getEstadoPago());
        return FacturacionMapper.toDTO(repo.save(existente));
    }

    public String eliminar(Long id) {
        facturacion existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id " + id));
        bloquearSiProcesada(existente);
        repo.deleteById(id);
        return "Factura eliminada correctamente";
    }

    private void bloquearSiProcesada(facturacion f) {
        if (!"Pendiente".equalsIgnoreCase(f.getEstadoPago())) {
            throw new IllegalStateException("La factura ya fue procesada y es inmutable.");
        }
    }
}
