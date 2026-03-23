package com.uamishop.catalogo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Estructura estándar para respuestas de error del microservicio de catálogo.
 * Copiado del shared kernel del monolito — en microservicios cada servicio
 * es autónomo y define su propia representación de errores HTTP.
 */
@Schema(description = "Estructura estándar para respuestas de error")
public record ApiError(
    @Schema(description = "Código de estado HTTP", example = "400")
    int status,

    @Schema(description = "Mensaje descriptivo del error", example = "El producto solicitado no existe")
    String message,

    @Schema(description = "Fecha y hora en que ocurrió el error")
    LocalDateTime timestamp
) {
    public ApiError(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
