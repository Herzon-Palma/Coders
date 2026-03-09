package com.uamishop.ventas.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para agregar un producto al carrito")
public record CarritoRequest(
        @Schema(description = "ID del producto a agregar", example = "123e4567-e89b-12d3-a456-426614174000") @NotNull(message = "El productoId es obligatorio") UUID productoId,

        @Schema(description = "Cantidad del producto (entre 1 y 10)", example = "2", minimum = "1", maximum = "10") @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad mínima es 1 (RN-VEN-01)") @Max(value = 10, message = "La cantidad máxima es 10 (RN-VEN-02)") Integer cantidad) {
}
