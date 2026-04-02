package com.uamishop.ventas.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResumen(
    UUID id,
    String sku,
    String nombre,
    BigDecimal precio,
    String moneda,
    boolean disponible
) {}
