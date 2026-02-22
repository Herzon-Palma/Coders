package com.uamishop.catalogo.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para crear o actualizar una categoría")
public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio") @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres") String nombre,

        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres") String descripcion) {

}
