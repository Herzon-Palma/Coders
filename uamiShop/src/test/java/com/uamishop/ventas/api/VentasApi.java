package com.uamishop.ventas.api;

import java.util.Optional;
import java.util.UUID;

/**
 * API pública del módulo Ventas.
 * ÚNICO punto de entrada para otros módulos.
 */
public interface VentasApi {
    CarritoResumen obtenerCarrito(UUID carritoId);

    Optional<CarritoResumen> obtenerCarritoActivoDeCliente(UUID clienteId);

    void completarCheckout(UUID carritoId);

    void abandonarCarrito(UUID carritoId);
}
