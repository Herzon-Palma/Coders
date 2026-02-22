package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con los detalles del carrito de compras")
public record CarritoResponse(

        @Schema(description = "ID único del carrito", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,

        @Schema(description = "ID del cliente propietario del carrito", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7") UUID clienteId,

        @Schema(description = "Estado actual del carrito", example = "ACTIVO", allowableValues = {
                "ACTIVO", "CHECKOUT", "COMPLETADO", "ABANDONADO" }) String estado,

        @Schema(description = "Lista de productos en el carrito") List<ItemResponse> items,

        @Schema(description = "Total calculado del carrito", example = "350.00") BigDecimal total,

        @Schema(description = "Moneda del total", example = "MXN") String moneda) {

}
