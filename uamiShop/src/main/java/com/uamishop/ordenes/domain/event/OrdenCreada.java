package com.uamishop.ordenes.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.valueobject.Money;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an order is created.
 */
public final class OrdenCreada implements DomainEvent {

    private final UUID ordenId;
    private final UUID customerId;
    private final Money total;
    private final Instant occurredOn;

    public OrdenCreada(UUID ordenId, UUID customerId, Money total) {
        this.ordenId = ordenId;
        this.customerId = customerId;
        this.total = total;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getOrdenId() {
        return ordenId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getTotal() {
        return total;
    }
}
