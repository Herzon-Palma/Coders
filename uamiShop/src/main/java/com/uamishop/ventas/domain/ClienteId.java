package com.uamishop.ventas.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: referencia al cliente propietario del carrito.
 * Pertenece al BC Ventas, sin acoplar al contexto de Identidad.
 */
public record ClienteId(UUID value) {

    public ClienteId {
        Objects.requireNonNull(value, "ClienteId.value no puede ser null");
    }

    public static ClienteId of(String raw) {
        Objects.requireNonNull(raw, "ClienteId no puede ser null");
        return new ClienteId(UUID.fromString(raw.trim()));
    }

    public static ClienteId generar() {
        return new ClienteId(UUID.randomUUID());
    }
}
