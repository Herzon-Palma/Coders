package com.uamishop.catalogo.api;

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
