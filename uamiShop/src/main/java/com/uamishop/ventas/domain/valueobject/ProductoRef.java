package com.uamishop.ventas.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a product reference within the cart.
 * Contains the product ID, name, and unit price at the time of adding to cart.
 */
public final class ProductoRef {

    private final UUID productId;
    private final String name;
    private final Money unitPrice;

    public ProductoRef(UUID productId, String name, Money unitPrice) {
        if (productId == null) {
            throw new BusinessRuleViolation("INVALID_PRODUCT_REF", "Product ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolation("INVALID_PRODUCT_REF", "Product name cannot be null or empty");
        }
        if (unitPrice == null) {
            throw new BusinessRuleViolation("INVALID_PRODUCT_REF", "Unit price cannot be null");
        }
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public static ProductoRef of(ProductoId productoId, String name, Money unitPrice) {
        return new ProductoRef(productoId.getValue(), name, unitPrice);
    }

    public UUID getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductoRef that = (ProductoRef) o;
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "ProductoRef{productId=" + productId + ", name='" + name + "', unitPrice=" + unitPrice + "}";
    }
}
