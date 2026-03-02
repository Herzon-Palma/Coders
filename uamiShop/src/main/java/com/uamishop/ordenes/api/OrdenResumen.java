package com.uamishop.ordenes.api;

import java.util.List;
import java.util.UUID;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ClienteId;

/**
 * DTO inter-módulo: resumen de la orden para consumo externo.
 * NO expone el Aggregate Root Orden ni sus entidades internas.
 */
public record OrdenResumen(
        UUID ordenId,
        ClienteId clienteId,
        String estadoOrden,
        Money subtotal,
        Money total,
        List<ItemOrdenResumen> items) {
    public record ItemOrdenResumen(
            Productoid productoId,
            String sku,
            String nombreProducto,
            int cantidad,
            Money precioUnitario) {
    }
}
