package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponse(UUID productoid, String nombreProducto, BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {

}
