package com.uamishop.ventas.domain.aggregate;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import com.uamishop.ventas.domain.valueobject.ProductoRef;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an item within the Cart aggregate.
 * Each item references a product and has a quantity.
 */
public class ItemCarrito {

    private final ProductoRef product;
    private int qty;

    public ItemCarrito(ProductoRef product, int qty) {
        if (product == null) {
            throw new BusinessRuleViolation("INVALID_ITEM", "Product reference cannot be null");
        }
        if (qty <= 0) {
            throw new BusinessRuleViolation("INVALID_QUANTITY", "Quantity must be greater than zero");
        }
        this.product = product;
        this.qty = qty;
    }

    /**
     * Increases the quantity by a delta amount.
     */
    public void increase(int delta) {
        if (delta <= 0) {
            throw new BusinessRuleViolation("INVALID_DELTA", "Delta must be greater than zero");
        }
        this.qty += delta;
    }

    /**
     * Changes the quantity to a new value.
     */
    public void changeQty(int newQty) {
        if (newQty <= 0) {
            throw new BusinessRuleViolation("INVALID_QUANTITY", "Quantity must be greater than zero");
        }
        this.qty = newQty;
    }

    /**
     * Calculates the total price for this line item.
     */
    public Money lineTotal() {
        return product.getUnitPrice().multiply(qty);
    }

    public ProductoRef getProduct() {
        return product;
    }

    public UUID getProductId() {
        return product.getProductId();
    }

    public String getProductName() {
        return product.getName();
    }

    public Money getUnitPrice() {
        return product.getUnitPrice();
    }

    public int getQty() {
        return qty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemCarrito that = (ItemCarrito) o;
        return product.getProductId().equals(that.product.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getProductId());
    }

    @Override
    public String toString() {
        return "ItemCarrito{product=" + product.getName() + ", qty=" + qty + ", total=" + lineTotal() + "}";
    }
}
