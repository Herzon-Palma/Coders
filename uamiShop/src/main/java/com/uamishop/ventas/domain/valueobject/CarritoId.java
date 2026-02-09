package com.uamishop.ventas.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Cart identifier.
 */
public final class CarritoId {

    private final UUID value;

    public CarritoId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "CarritoId cannot be null");
        }
        this.value = value;
    }

    public static CarritoId generate() {
        return new CarritoId(UUID.randomUUID());
    }

    public static CarritoId from(String value) {
        return new CarritoId(UUID.fromString(value));
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
        CarritoId carritoId = (CarritoId) o;
        return value.equals(carritoId.value);
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
