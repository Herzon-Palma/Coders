package com.uamishop.ventas.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Product identifier (reference by identity).
 * This is not the Product entity itself, just an ID reference.
 */
public final class ProductoId {

    private final UUID value;

    public ProductoId(UUID value) {
        if (value == null) {
            throw new BusinessRuleViolation("INVALID_ID", "ProductoId cannot be null");
        }
        this.value = value;
    }

    public static ProductoId generate() {
        return new ProductoId(UUID.randomUUID());
    }

    public static ProductoId from(String value) {
        return new ProductoId(UUID.fromString(value));
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
        ProductoId that = (ProductoId) o;
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
