package com.uamishop.ordenes.controller;

import java.util.List;
import java.util.UUID;

public record OrdenResponse(
        UUID ordenId,
        String estadoOrden,
        UUID clienteId,
        List<ItemOrdenResponse> items) {

    public record ItemOrdenResponse(
            UUID productoId,
            String sku,
            String nombreProducto,
            int cantidad,
            String imagenUrl) {
    }
}
