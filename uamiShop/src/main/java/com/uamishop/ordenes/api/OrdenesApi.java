package com.uamishop.ordenes.api;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * API pública del módulo Órdenes.
 * ÚNICO punto de entrada para otros módulos.
 */
public interface OrdenesApi {
    Optional<OrdenResumen> obtenerOrden(UUID ordenId);

    List<OrdenResumen> obtenerOrdenesPorCliente(UUID clienteId);
}
