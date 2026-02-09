package com.uamishop.checkout.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when checkout fails.
 */
public final class CheckoutFallido implements DomainEvent {

    private final UUID checkoutId;
    private final String reason;
    private final Instant occurredOn;

    public CheckoutFallido(UUID checkoutId, String reason) {
        this.checkoutId = checkoutId;
        this.reason = reason;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCheckoutId() {
        return checkoutId;
    }

    public String getReason() {
        return reason;
    }
}
