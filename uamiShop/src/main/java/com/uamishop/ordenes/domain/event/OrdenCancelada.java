package com.uamishop.ordenes.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an order is cancelled.
 */
public final class OrdenCancelada implements DomainEvent {

    private final UUID ordenId;
    private final String reason;
    private final Instant occurredOn;

    public OrdenCancelada(UUID ordenId, String reason) {
        this.ordenId = ordenId;
        this.reason = reason;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getOrdenId() {
        return ordenId;
    }

    public String getReason() {
        return reason;
    }
}
