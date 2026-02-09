package com.uamishop.ordenes.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an order is shipped.
 */
public final class OrdenEnviada implements DomainEvent {

    private final UUID ordenId;
    private final String trackingNumber;
    private final String carrier;
    private final Instant occurredOn;

    public OrdenEnviada(UUID ordenId, String trackingNumber, String carrier) {
        this.ordenId = ordenId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getOrdenId() {
        return ordenId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }
}
