package com.uamishop.ventas.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Petición para agregar un producto al carrito")
public record CarritoRequest(

        @NotNull(message = "El ID del producto es obligatorio") @Schema(description = "UUID del producto a agregar", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID productoId,

        @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") // RN-VEN-01
        @Max(value = 10, message = "La cantidad máxima por producto es 10") // RN-VEN-02
        @Schema(description = "Cantidad de unidades a agregar (1-10)", example = "2") Integer cantidad) {

}
