package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con los datos del carrito de compras")
public record CarritoResponse(
                @Schema(description = "ID único del carrito", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890") UUID id,

                @Schema(description = "ID del cliente propietario del carrito", example = "123e4567-e89b-12d3-a456-426614174000") UUID clienteId,

                @Schema(description = "Estado actual del carrito", example = "ACTIVO", allowableValues = {
                                "ACTIVO", "CHECKOUT", "COMPLETADO", "ABANDONADO" }) String estado,

                @Schema(description = "Lista de productos en el carrito") List<ItemResponse> items,

                @Schema(description = "Total del carrito con descuentos aplicados", example = "350.00") BigDecimal total,

                @Schema(description = "Moneda del total", example = "MXN") String moneda) {
}
