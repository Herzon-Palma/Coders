package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing an Order identifier (reference to Ordenes BC).
 */
public final class OrderId {

    private final UUID value;

    public OrderId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "OrderId cannot be null");
        }
        this.value = value;
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId from(String value) {
        return new OrderId(UUID.fromString(value));
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
        OrderId that = (OrderId) o;
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
