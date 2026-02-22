package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CarritoResponse(UUID id, UUID clienteId, String estado, List<ItemResponse> items, BigDecimal total, String moneda) {

}
