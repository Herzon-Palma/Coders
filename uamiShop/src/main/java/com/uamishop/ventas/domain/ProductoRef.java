package com.uamishop.ventas.domain;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record ProductoRef(UUID productoId, String nombre) {
    public ProductoRef {
        if (productoId == null) throw new IllegalArgumentException("El ID del producto es obligatorio");
    }
}
