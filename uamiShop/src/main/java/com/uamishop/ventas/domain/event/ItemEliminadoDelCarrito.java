package com.uamishop.ventas.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an item is removed from the cart.
 */
public final class ItemEliminadoDelCarrito implements DomainEvent {

    private final UUID carritoId;
    private final UUID productoId;
    private final Instant occurredOn;

    public ItemEliminadoDelCarrito(UUID carritoId, UUID productoId) {
        this.carritoId = carritoId;
        this.productoId = productoId;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCarritoId() {
        return carritoId;
    }

    public UUID getProductoId() {
        return productoId;
    }
}
