package com.uamishop.ordenes.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an order is delivered.
 */
public final class OrdenEntregada implements DomainEvent {

    private final UUID ordenId;
    private final Instant occurredOn;

    public OrdenEntregada(UUID ordenId) {
        this.ordenId = ordenId;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getOrdenId() {
        return ordenId;
    }
}
