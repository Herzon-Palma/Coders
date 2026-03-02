package com.uamishop.ventas.api;

import com.uamishop.shared.domain.ClienteId;

import java.util.List;
import java.util.UUID;

/**
 * DTO inter-módulo: resumen del carrito para consumo externo.
 * NO expone el Aggregate Root Carrito ni sus entidades internas.
 */
public record CarritoResumen(
                UUID carritoId,
                ClienteId clienteId,
                String estado,
                List<ItemCarritoResumen> items) {
}
