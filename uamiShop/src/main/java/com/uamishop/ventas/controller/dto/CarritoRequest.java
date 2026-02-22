package com.uamishop.ventas.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CarritoRequest(
        @NotNull(message = "El ID del producto es obligatorio") UUID productoId,

        @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {

}
