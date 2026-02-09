package com.uamishop.ventas.domain;

/**
 * Estados del carrito de compras.
 * Máquina de estados según State_Carrito.puml:
 * - ACTIVO: permite operaciones de items
 * - EN_CHECKOUT: carrito congelado para checkout
 * - ABANDONADO: estado terminal (abandono o compra completada)
 */
public enum EstadoCarrito {
    ACTIVO,
    EN_CHECKOUT,
    ABANDONADO
}
