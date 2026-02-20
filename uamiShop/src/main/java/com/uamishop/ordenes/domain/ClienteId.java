package com.uamishop.ordenes.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: identificador único del cliente.
 * Referencia al Bounded Context de Identidad sin acoplamiento directo.
 */
public record ClienteId(UUID value) {

    public ClienteId {
        Objects.requireNonNull(value, "ClienteId.value no puede ser null");
    }

    public static ClienteId newId() {
        return new ClienteId(UUID.randomUUID());
    }

    public static ClienteId fromString(String raw) {
        Objects.requireNonNull(raw, "ClienteId no puede ser null");
        return new ClienteId(UUID.fromString(raw.trim()));
    }
}
