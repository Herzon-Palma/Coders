package com.uamishop.catalogo.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para crear o actualizar un producto")
public record ProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio") @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres") String nombre,

        @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres") String descripcion,

        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero") BigDecimal precio,

        @NotBlank(message = "La moneda es obligatoria") @Size(min = 3, max = 3, message = "La moneda debe ser un código de 3 caracteres (ej. MXN)") String moneda,

        @NotNull(message = "La categoría es obligatoria") UUID categoriaid) {

}
