package com.uamishop.catalogo.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;


public record CategoriaResponse(
    
    @Schema(description = "ID único de la categoría")
    UUID id,
    
    @Schema(description = "Nombre de la categoría", example = "Ropa")
    String nombre,
    
    @Schema(description = "Descripción detallada de la categoría", example = "Ropa para hombres y mujeres de todas las edades")
    String descripcion,
    
    UUID categoriaid) {

}
