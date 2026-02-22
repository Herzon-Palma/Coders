package com.uamishop.catalogo.controller.dto;
import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;


public record ProductoResponse(
    @Schema(description = "Identificador único del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id, 
    
    @Schema(description = "Nombre del producto", example = "Camiseta de algodón")
    String nombre,

    @Schema(description = "Descripción detallada del producto", example = "Camiseta de algodón 100% con estampado frontal")
    String descripcion, 
    
    BigDecimal precio, 
    
    @Schema(description = "Moneda del precio del producto", example = "MXN")
    String moneda, 
    UUID categoriaid) {

}
