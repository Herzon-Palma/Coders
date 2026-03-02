package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un producto dentro del carrito")
public record ItemResponse(
        @Schema(description = "ID del producto", example = "123e4567-e89b-12d3-a456-426614174000") UUID productoid,

        @Schema(description = "Nombre del producto", example = "Camiseta de algodón") String nombreProducto,

        @Schema(description = "Cantidad del producto en el carrito", example = "3") BigDecimal cantidad,

        @Schema(description = "Precio unitario del producto", example = "150.00") BigDecimal precioUnitario,

        @Schema(description = "Subtotal del item (cantidad × precio unitario)", example = "450.00") BigDecimal subtotal) {
}
