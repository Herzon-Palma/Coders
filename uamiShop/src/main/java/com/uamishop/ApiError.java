package com.uamishop;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

//Estructura para respuestas de error
@Schema(description = "Estructura estándar para respuestas de error")
public record ApiError(
        @Schema(description = "Código de estado HTTP", example = "400") int status,

        @Schema(description = "Nombre corto del error", example = "Bad Request") String error,

        @Schema(description = "Mensaje detallado del error", example = "El nombre del producto es obligatorio") String message,

        @Schema(description = "Ruta donde ocurrió el error", example = "/api/productos") String path,

        @Schema(description = "Fecha y hora en que ocurrió el error") LocalDateTime timestamp) {
    public ApiError(int status, String error, String message, String path) {
        this(status, error, message, path, LocalDateTime.now());
    }
}
