package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.exception.ReglaNegocioException;

import java.util.Objects;
import java.util.UUID;

public record OrdenId(UUID value) {

    public OrdenId {
        Objects.requireNonNull(value, "OrdenId.value no puede ser null");
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
