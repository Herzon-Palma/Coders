package com.uamishop.ventas.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: identificador único de un item dentro del carrito.
 */
public record ItemCarritoId(UUID value) {

    public ItemCarritoId {
        Objects.requireNonNull(value, "ItemCarritoId.value no puede ser null");
    }

    public static ItemCarritoId generar() {
        return new ItemCarritoId(UUID.randomUUID());
    }

    public static ItemCarritoId fromString(String raw) {
        Objects.requireNonNull(raw, "ItemCarritoId no puede ser null");
        return new ItemCarritoId(UUID.fromString(raw.trim()));
    }
}
