package com.uamishop.ventas.domain.event;

import com.uamishop.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when an item is added to the cart.
 */
public final class ItemAgregadoAlCarrito implements DomainEvent {

    private final UUID carritoId;
    private final UUID productoId;
    private final String nombreProducto;
    private final int cantidad;
    private final Instant occurredOn;

    public ItemAgregadoAlCarrito(UUID carritoId, UUID productoId, String nombreProducto, int cantidad) {
        this.carritoId = carritoId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }
}
