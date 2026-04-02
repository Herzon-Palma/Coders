package com.uamishop.ordenes.client.dto;

import java.util.List;
import java.util.UUID;

import com.uamishop.shared.domain.Productoid;

public record CarritoResumen(
    UUID carritoId,
    UUID clienteId,
    String estado,
    List<ItemCarritoResumen> items
) {
    public record ItemCarritoResumen(
        Productoid productoId,
        int cantidad
    ) {}
}
