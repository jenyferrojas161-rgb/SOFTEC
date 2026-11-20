package com.softech.softech.DTOs;

/**
 * Solicitud de servicio escrita "a mano": el cliente (nuevo o existente) y el equipo se escriben
 * en el formulario, no se eligen de listas con datos ya guardados.
 */
public record SolicitudServicioRequest(
        Long idCliente,
        String clienteNombre,
        String clienteDocumento,
        String clienteTelefono,
        String clienteEmail,
        String equipoTipo,
        String equipoMarca,
        String equipoModelo,
        String equipoSerie,
        String tipoServicio,
        String problema,
        String prioridad,
        Long idTecnico
) {}
