package com.softech.softech.DTOs;

public record EstadoServicioDTO(
        Long idServicio,
        Long idEquipo,
        String numeroSerie,
        String tipoEquipo,
        String marca,
        String modelo,
        String estadoEquipo,
        String estadoServicio,
        String problemaReportado,
        String diagnosticoRealizado,
        String fechaIngreso,
        String fechaEntrega
) {}
