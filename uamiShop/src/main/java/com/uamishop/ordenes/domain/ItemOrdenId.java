package com.uamishop.ordenes.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: identificador único de un item dentro de una orden.
 */
public record ItemOrdenId(UUID value) {

    public ItemOrdenId {
        Objects.requireNonNull(value, "ItemOrdenId.value no puede ser null");
    }

    public static ItemOrdenId newId() {
        return new ItemOrdenId(UUID.randomUUID());
    }

    public static ItemOrdenId fromString(String raw) {
        Objects.requireNonNull(raw, "ItemOrdenId no puede ser null");
        return new ItemOrdenId(UUID.fromString(raw.trim()));
    }
}
