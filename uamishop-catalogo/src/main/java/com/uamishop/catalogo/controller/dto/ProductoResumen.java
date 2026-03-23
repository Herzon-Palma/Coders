package com.uamishop.catalogo.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResumen(
    UUID id,
    String nombre,
    String sku,
    BigDecimal precio,
    String moneda,
    UUID categoriaId,
    String categoriaNombre,
    boolean disponible
) {

}
