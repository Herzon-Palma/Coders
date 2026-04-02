package com.uamishop.ordenes.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: identificador único de un item dentro de una orden.
 */
@Embeddable
public record ItemOrdenId(UUID id) implements Serializable {

    public ItemOrdenId {
        Objects.requireNonNull(id, "ItemOrdenId.id no puede ser null");
    }

    public static ItemOrdenId newId() {
        return new ItemOrdenId(UUID.randomUUID());
    }

    public static ItemOrdenId fromString(String raw) {
        Objects.requireNonNull(raw, "ItemOrdenId no puede ser null");
        return new ItemOrdenId(UUID.fromString(raw.trim()));
    }
}
