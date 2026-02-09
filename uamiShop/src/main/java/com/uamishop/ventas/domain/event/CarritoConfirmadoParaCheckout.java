package com.uamishop.ventas.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.valueobject.Money;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when the cart is confirmed for checkout.
 */
public final class CarritoConfirmadoParaCheckout implements DomainEvent {

    private final UUID carritoId;
    private final UUID usuarioId;
    private final int totalItems;
    private final Money subtotal;
    private final Instant occurredOn;

    public CarritoConfirmadoParaCheckout(UUID carritoId, UUID usuarioId, int totalItems, Money subtotal) {
        this.carritoId = carritoId;
        this.usuarioId = usuarioId;
        this.totalItems = totalItems;
        this.subtotal = subtotal;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCarritoId() {
        return carritoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public Money getSubtotal() {
        return subtotal;
    }
}
