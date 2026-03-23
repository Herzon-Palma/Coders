package com.uamishop.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record ProductoAgregadoAlCarritoEvent(
    UUID eventoId,
    Instant occurredAt,
    UUID productoId,
    UUID carritoId,
    int cantidad,
    BigDecimal precioUnitario,
    String moneda
) {

}
