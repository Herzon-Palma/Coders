package com.uamishop.catalogo.controller.dto;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.*;

public record ProductoRequest(
    @NotEmpty(message = "El nombre no puede estar vacío") String nombre,
    @NotEmpty(message = "La descripción no puede estar vacía") String descripcion,
    @NotEmpty(message = "El SKU no puede estar vacío") String sku,
    BigDecimal precio,
    @NotEmpty String moneda,
    @NotNull UUID categoriaid) {

}
