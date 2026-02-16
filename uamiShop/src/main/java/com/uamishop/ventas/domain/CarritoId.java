package com.uamishop.ventas.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: identificador único del carrito.
 */
public record CarritoId(UUID value) {

    public CarritoId {
        Objects.requireNonNull(value, "CarritoId.value no puede ser null");
    }

    public static CarritoId generar() {
        return new CarritoId(UUID.randomUUID());
    }

    public static CarritoId fromString(String raw) {
        Objects.requireNonNull(raw, "CarritoId no puede ser null");
        return new CarritoId(UUID.fromString(raw.trim()));
    }
}
