package com.uamishop.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;



public record ProductoCompradoEvent(
    UUID eventoId,
    Instant occurredAt,
    UUID ordenId,
    UUID clienteId,
    List<ProductoComprado> productos
) {
    public record ProductoComprado(
        UUID productoId,
        String sku,
        int cantidad,
        BigDecimal precioUnitario,
        String moneda
    ) {
    }
}
