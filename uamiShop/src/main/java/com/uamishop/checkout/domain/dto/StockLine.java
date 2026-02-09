package com.uamishop.checkout.domain.dto;

import java.util.UUID;

/**
 * DTO representing a stock validation request line.
 */
public final class StockLine {

    private final UUID productId;
    private final int qty;

    public StockLine(UUID productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public static StockLine from(CheckoutLine line) {
        return new StockLine(line.getProductId(), line.getQty());
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQty() {
        return qty;
    }
}
