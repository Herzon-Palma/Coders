package com.uamishop.ordenes.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing an Order identifier.
 */
public final class OrdenId {

    private final UUID value;

    public OrdenId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "OrdenId cannot be null");
        }
        this.value = value;
    }

    public static OrdenId generate() {
        return new OrdenId(UUID.randomUUID());
    }

    public static OrdenId from(String value) {
        return new OrdenId(UUID.fromString(value));
    }

    public static OrdenId from(UUID value) {
        return new OrdenId(value);
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
        OrdenId ordenId = (OrdenId) o;
        return value.equals(ordenId.value);
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
