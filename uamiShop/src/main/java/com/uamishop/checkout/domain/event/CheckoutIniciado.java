package com.uamishop.checkout.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a checkout is initiated.
 */
public final class CheckoutIniciado implements DomainEvent {

    private final UUID checkoutId;
    private final UUID carritoId;
    private final UUID customerId;
    private final Instant occurredOn;

    public CheckoutIniciado(UUID checkoutId, UUID carritoId, UUID customerId) {
        this.checkoutId = checkoutId;
        this.carritoId = carritoId;
        this.customerId = customerId;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCheckoutId() {
        return checkoutId;
    }

    public UUID getCarritoId() {
        return carritoId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
