package com.uamishop.checkout.domain.dto;

import com.uamishop.shared.domain.valueobject.Money;
import java.util.UUID;

/**
 * DTO representing a line item in the checkout.
 */
public final class CheckoutLine {

    private final UUID productId;
    private final String name;
    private final Money unitPrice;
    private final int qty;
    private final Money lineTotal;

    public CheckoutLine(UUID productId, String name, Money unitPrice, int qty) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.qty = qty;
        this.lineTotal = unitPrice.multiply(qty);
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

    public int getQty() {
        return qty;
    }

    public Money getLineTotal() {
        return lineTotal;
    }
}
