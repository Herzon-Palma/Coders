package com.uamishop.checkout.domain.dto;

import com.uamishop.shared.domain.valueobject.Money;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a snapshot of the cart for checkout.
 * Received from Ventas BC.
 */
public final class CheckoutSummary {

    private final UUID cartId;
    private final UUID customerId;
    private final List<CheckoutLine> items;
    private final Money subtotal;

    public CheckoutSummary(UUID cartId, UUID customerId, List<CheckoutLine> items, Money subtotal) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
    }

    public UUID getCartId() {
        return cartId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<CheckoutLine> getItems() {
        return items;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int itemCount() {
        return items.size();
    }
}
