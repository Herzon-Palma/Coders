package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.exception.ReglaNegocioException;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public record OrdenId(UUID id) implements Serializable {

    public OrdenId {
        Objects.requireNonNull(id, "OrdenId.id no puede ser null");
    }

    public static OrdenId newId() {
        return new OrdenId(UUID.randomUUID());
    }

    public static OrdenId fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ReglaNegocioException("RN-ORD-ID-01", "OrdenId no puede ser vacío");
        }
        try {
            return new OrdenId(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            throw new ReglaNegocioException("RN-ORD-ID-02", "OrdenId inválido: " + raw);
        }
    }
}
