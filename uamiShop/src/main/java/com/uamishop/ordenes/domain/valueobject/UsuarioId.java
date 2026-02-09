package com.uamishop.ordenes.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a User identifier (reference from Identity BC).
 */
public final class UsuarioId {

    private final UUID value;

    public UsuarioId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "UsuarioId cannot be null");
        }
        this.value = value;
    }

    public static UsuarioId from(String value) {
        return new UsuarioId(UUID.fromString(value));
    }

    public static UsuarioId from(UUID value) {
        return new UsuarioId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return value.equals(usuarioId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
