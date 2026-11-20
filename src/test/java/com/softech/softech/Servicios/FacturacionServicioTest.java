package com.softech.softech.Servicios;

import com.softech.softech.DTOs.FacturacionDTO;
import com.softech.softech.modelo.facturacion;
import com.softech.softech.repositorio.reposi_facturacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturacionServicioTest {

    @Mock private reposi_facturacion repo;
    @Mock private JdbcTemplate jdbc;
    @InjectMocks private FacturacionServicio servicio;

    private facturacion factura(String estado) {
        facturacion f = new facturacion();
        f.setId(1L);
        f.setIdServicio(10L);
        f.setIdCliente(5L);
        f.setNumeroFactura("F-0001");
        f.setFechaFactura(LocalDate.of(2026, 10, 1));
        f.setTotalPagar(150000.0);
        f.setEstadoPago(estado);
        return f;
    }

    private FacturacionDTO dto() {
        return new FacturacionDTO(1L, 10L, 5L, "F-0001", LocalDate.of(2026, 10, 1), null,
                "Factura", 200000.0, "Efectivo", "Pendiente");
    }

    @Test
    void noPermiteModificarUnaFacturaPagada() {
        when(repo.findById(1L)).thenReturn(Optional.of(factura("Pagado")));
        var e = assertThrows(IllegalStateException.class, () -> servicio.actualizar(1L, dto()));
        assertTrue(e.getMessage().contains("inmutable"));
        verify(repo, never()).save(any());
    }

    @Test
    void noPermiteEliminarUnaFacturaPagada() {
        when(repo.findById(1L)).thenReturn(Optional.of(factura("Pagado")));
        assertThrows(IllegalStateException.class, () -> servicio.eliminar(1L));
        verify(repo, never()).deleteById(any());
    }

    @Test
    void permiteEliminarUnaFacturaPendiente() {
        when(repo.findById(1L)).thenReturn(Optional.of(factura("Pendiente")));
        assertEquals("Factura eliminada correctamente", servicio.eliminar(1L));
        verify(repo).deleteById(1L);
    }

    @Test
    void permiteActualizarUnaFacturaPendiente() {
        when(repo.findById(1L)).thenReturn(Optional.of(factura("Pendiente")));
        when(repo.save(any(facturacion.class))).thenAnswer(inv -> inv.getArgument(0));
        FacturacionDTO resultado = servicio.actualizar(1L, dto());
        assertEquals(200000.0, resultado.getTotalPagar());
        verify(repo).save(any(facturacion.class));
    }
}
