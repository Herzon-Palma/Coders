package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Cart identifier (reference from Ventas BC).
 */
public final class CarritoId {

    private final UUID value;

    public CarritoId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "CarritoId cannot be null");
        }
        this.value = value;
    }

    public static CarritoId from(String value) {
        return new CarritoId(UUID.fromString(value));
    }

    public static CarritoId from(UUID value) {
        return new CarritoId(value);
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
        CarritoId that = (CarritoId) o;
        return value.equals(that.value);
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
