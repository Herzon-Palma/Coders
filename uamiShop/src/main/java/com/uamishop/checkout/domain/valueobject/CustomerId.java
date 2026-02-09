package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Customer identifier (reference from Identity BC).
 */
public final class CustomerId {

    private final UUID value;

    public CustomerId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "CustomerId cannot be null");
        }
        this.value = value;
    }

    public static CustomerId from(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    public static CustomerId from(UUID value) {
        return new CustomerId(value);
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
        CustomerId that = (CustomerId) o;
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
