package com.uamishop.catalogo.controller.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estadísticas de ventas y actividad de un producto")
public record ProductoEstadisticasResponse(

        @Schema(description = "Número total de transacciones de venta del producto", example = "42") long ventasTotales,

        @Schema(description = "Cantidad total de unidades vendidas del producto", example = "150") long cantidadVendida,

        @Schema(description = "Número de veces que el producto fue agregado a un carrito", example = "230") long vecesAgregadoAlCarrito,

        @Schema(description = "Fecha y hora de la última venta del producto", example = "2026-03-08T07:30:00Z") Instant ultimaVentaAt) {
}
