package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;

/**
 * Value Object representing a coupon code.
 */
public final class CodigoCupon {

    private final String value;

    public CodigoCupon(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolation("INVALID_COUPON", "Coupon code cannot be null or empty");
        }
        this.value = value.toUpperCase().trim();
    }

    public static CodigoCupon of(String value) {
        return new CodigoCupon(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CodigoCupon that = (CodigoCupon) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
