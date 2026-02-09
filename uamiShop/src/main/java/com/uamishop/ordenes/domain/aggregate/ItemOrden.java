package com.uamishop.ordenes.domain.aggregate;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an item within the Order aggregate.
 */
public class ItemOrden {

    private final UUID productId;
    private final String name;
    private final Money unitPrice;
    private final int quantity;

    public ItemOrden(UUID productId, String name, Money unitPrice, int quantity) {
        if (productId == null) {
            throw new BusinessRuleViolation("INVALID_ITEM", "Product ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ITEM", "Product name is required");
        }
        if (unitPrice == null) {
            throw new BusinessRuleViolation("INVALID_ITEM", "Unit price is required");
        }
        if (quantity <= 0) {
            throw new BusinessRuleViolation("INVALID_ITEM", "Quantity must be greater than zero");
        }
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Money lineTotal() {
        return unitPrice.multiply(quantity);
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

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemOrden itemOrden = (ItemOrden) o;
        return productId.equals(itemOrden.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " @ " + unitPrice;
    }
}
