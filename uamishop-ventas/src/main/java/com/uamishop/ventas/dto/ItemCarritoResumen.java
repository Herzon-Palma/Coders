package com.uamishop.ventas.dto;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;

/**
 * DTO inter-módulo: detalle de un item en el resumen del carrito.
 */
public record ItemCarritoResumen(
        Productoid productoId,
        String nombreProducto,
        String sku,
        int cantidad,
        Money precioUnitario) {
}
