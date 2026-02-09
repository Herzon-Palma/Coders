package com.uamishop.checkout.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.valueobject.Money;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when payment is approved.
 */
public final class CheckoutPagado implements DomainEvent {

    private final UUID checkoutId;
    private final String providerRef;
    private final Money amount;
    private final Instant occurredOn;

    public CheckoutPagado(UUID checkoutId, String providerRef, Money amount) {
        this.checkoutId = checkoutId;
        this.providerRef = providerRef;
        this.amount = amount;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCheckoutId() {
        return checkoutId;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public Money getAmount() {
        return amount;
    }
}
