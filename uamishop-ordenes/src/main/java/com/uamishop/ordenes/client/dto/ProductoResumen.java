package com.uamishop.ordenes.client.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record ProductoResumen(
    UUID id,
    String sku,
    String nombre,
    BigDecimal precio,
    String moneda,
    boolean disponible,
    List<ImagenResumen> imagenes
) {}
