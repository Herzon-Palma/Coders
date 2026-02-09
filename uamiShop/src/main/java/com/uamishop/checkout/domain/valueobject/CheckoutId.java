package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Checkout identifier.
 */
public final class CheckoutId {

    private final UUID value;

    public CheckoutId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "CheckoutId cannot be null");
        }
        this.value = value;
    }

    public static CheckoutId generate() {
        return new CheckoutId(UUID.randomUUID());
    }

    public static CheckoutId from(String value) {
        return new CheckoutId(UUID.fromString(value));
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
        CheckoutId that = (CheckoutId) o;
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
