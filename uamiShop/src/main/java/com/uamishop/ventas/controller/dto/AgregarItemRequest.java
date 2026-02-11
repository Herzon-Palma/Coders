package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

// DTO usando Java Records
public record AgregarItemRequest(
    UUID productoId,
    String nombre,
    BigDecimal precio,
    int cantidad
) {}
