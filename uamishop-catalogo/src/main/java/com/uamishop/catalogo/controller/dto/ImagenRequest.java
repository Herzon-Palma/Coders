package com.uamishop.catalogo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Imagen asociada a un producto")
public record ImagenRequest(
        @Schema(description = "URL pública de la imagen (http/https)")
        @NotEmpty String url,

        @Schema(description = "Texto alternativo")
        @NotEmpty String altText,

        @Schema(description = "Orden de despliegue (1..N)")
        @NotNull Integer orden) {
}
