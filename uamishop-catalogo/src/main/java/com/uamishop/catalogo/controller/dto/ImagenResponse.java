package com.uamishop.catalogo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Imagen asociada a un producto")
public record ImagenResponse(
        @Schema(description = "URL pública de la imagen (http/https)")
        String url,

        @Schema(description = "Texto alternativo")
        String altText,

        @Schema(description = "Orden de despliegue (1..N)")
        Integer orden) {
}
