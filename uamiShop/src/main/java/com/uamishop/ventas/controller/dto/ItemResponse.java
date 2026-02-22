package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un item dentro del carrito")
public record ItemResponse(

        @Schema(description = "ID del producto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID productoid,

        @Schema(description = "Nombre del producto", example = "Laptop HP Pavilion") String nombreProducto,

        @Schema(description = "Cantidad de unidades", example = "2") BigDecimal cantidad,

        @Schema(description = "Precio unitario del producto", example = "100.00") BigDecimal precioUnitario,

        @Schema(description = "Subtotal del item (cantidad × precio unitario)", example = "200.00") BigDecimal subtotal) {

}
