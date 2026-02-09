package com.uamishop.ventas.domain.aggregate;

import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import com.uamishop.ventas.domain.EstadoCarrito;
import com.uamishop.ventas.domain.event.CarritoConfirmadoParaCheckout;
import com.uamishop.ventas.domain.event.ItemAgregadoAlCarrito;
import com.uamishop.ventas.domain.event.ItemEliminadoDelCarrito;
import com.uamishop.ventas.domain.valueobject.CarritoId;
import com.uamishop.ventas.domain.valueobject.ProductoRef;
import com.uamishop.ventas.domain.valueobject.UsuarioId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate Root for the Cart domain.
 * Manages shopping cart items and state transitions according to
 * State_Carrito.puml.
 * 
 * State Machine:
 * - ACTIVO: Initial state, allows item operations
 * - EN_CHECKOUT: Frozen for checkout process
 * - ABANDONADO: Terminal state (abandoned or checkout completed)
 */
public class Carrito {

    private final CarritoId id;
    private final UsuarioId customerId;
    private EstadoCarrito state;
    private final List<ItemCarrito> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents;

    private Carrito(CarritoId id, UsuarioId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.state = EstadoCarrito.ACTIVO;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Factory method to create a new cart for a customer.
     */
    public static Carrito create(UsuarioId customerId) {
        if (customerId == null) {
            throw new BusinessRuleViolation("INVALID_CUSTOMER", "Customer ID cannot be null");
        }
        return new Carrito(CarritoId.generate(), customerId);
    }

    /**
     * Factory method with explicit cart ID.
     */
    public static Carrito create(CarritoId carritoId, UsuarioId customerId) {
        if (carritoId == null) {
            throw new BusinessRuleViolation("INVALID_CART", "Cart ID cannot be null");
        }
        if (customerId == null) {
            throw new BusinessRuleViolation("INVALID_CUSTOMER", "Customer ID cannot be null");
        }
        return new Carrito(carritoId, customerId);
    }

    /**
     * Adds an item to the cart or increases quantity if already present.
     * Only allowed in ACTIVO state.
     */
    public void addItem(ProductoRef producto, int qty) {
        requireState(EstadoCarrito.ACTIVO, "addItem");

        if (producto == null) {
            throw new BusinessRuleViolation("INVALID_PRODUCT", "Product reference cannot be null");
        }
        if (qty <= 0) {
            throw new BusinessRuleViolation("INVALID_QUANTITY", "Quantity must be greater than zero");
        }

        Optional<ItemCarrito> existingItem = findItem(producto.getProductId());

        if (existingItem.isPresent()) {
            existingItem.get().increase(qty);
        } else {
            items.add(new ItemCarrito(producto, qty));
        }

        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new ItemAgregadoAlCarrito(
                id.getValue(),
                producto.getProductId(),
                producto.getName(),
                qty));
    }

    /**
     * Updates the quantity of an existing item.
     * Only allowed in ACTIVO state.
     */
    public void updateQty(UUID productId, int newQty) {
        requireState(EstadoCarrito.ACTIVO, "updateQty");

        if (productId == null) {
            throw new BusinessRuleViolation("INVALID_PRODUCT", "Product ID cannot be null");
        }
        if (newQty <= 0) {
            throw new BusinessRuleViolation("INVALID_QUANTITY", "Quantity must be greater than zero");
        }

        ItemCarrito item = findItem(productId)
                .orElseThrow(() -> new BusinessRuleViolation("ITEM_NOT_FOUND",
                        "Item with product ID " + productId + " not found in cart"));

        item.changeQty(newQty);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Removes an item from the cart.
     * Only allowed in ACTIVO state.
     */
    public void removeItem(UUID productId) {
        requireState(EstadoCarrito.ACTIVO, "removeItem");

        if (productId == null) {
            throw new BusinessRuleViolation("INVALID_PRODUCT", "Product ID cannot be null");
        }

        ItemCarrito item = findItem(productId)
                .orElseThrow(() -> new BusinessRuleViolation("ITEM_NOT_FOUND",
                        "Item with product ID " + productId + " not found in cart"));

        items.remove(item);
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new ItemEliminadoDelCarrito(id.getValue(), productId));
    }

    /**
     * Clears all items from the cart.
     * Only allowed in ACTIVO state.
     */
    public void clear() {
        requireState(EstadoCarrito.ACTIVO, "clear");
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Starts the checkout process.
     * Transitions from ACTIVO to EN_CHECKOUT.
     */
    public void startCheckout() {
        requireState(EstadoCarrito.ACTIVO, "startCheckout");

        if (items.isEmpty()) {
            throw new BusinessRuleViolation("EMPTY_CART", "Cannot start checkout with an empty cart");
        }

        this.state = EstadoCarrito.EN_CHECKOUT;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new CarritoConfirmadoParaCheckout(
                id.getValue(),
                customerId.getValue(),
                totalItems(),
                subtotal()));
    }

    /**
     * Completes the checkout process.
     * Transitions from EN_CHECKOUT to ABANDONADO (terminal state after purchase).
     */
    public void completeCheckout() {
        requireState(EstadoCarrito.EN_CHECKOUT, "completeCheckout");
        this.state = EstadoCarrito.ABANDONADO;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Abandons the cart.
     * Can be called from ACTIVO or EN_CHECKOUT states.
     */
    public void abandon() {
        if (state == EstadoCarrito.ABANDONADO) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Cart is already abandoned");
        }
        this.state = EstadoCarrito.ABANDONADO;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the subtotal of all items in the cart.
     */
    public Money subtotal() {
        if (items.isEmpty()) {
            return Money.ZERO_MXN;
        }
        return items.stream()
                .map(ItemCarrito::lineTotal)
                .reduce(Money::add)
                .orElse(Money.ZERO_MXN);
    }

    /**
     * Returns the total number of items (sum of quantities).
     */
    public int totalItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getQty)
                .sum();
    }

    /**
     * Returns the count of distinct products in the cart.
     */
    public int distinctProducts() {
        return items.size();
    }

    /**
     * Checks if the cart contains a specific product.
     */
    public boolean containsProduct(UUID productId) {
        return findItem(productId).isPresent();
    }

    /**
     * Pulls all accumulated domain events and clears the internal list.
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // Private helper methods

    private Optional<ItemCarrito> findItem(UUID productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    private void requireState(EstadoCarrito requiredState, String operation) {
        if (this.state != requiredState) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Operation '" + operation + "' not allowed in state " + this.state +
                            ". Required state: " + requiredState);
        }
    }

    // Getters

    public CarritoId getId() {
        return id;
    }

    public UsuarioId getCustomerId() {
        return customerId;
    }

    public EstadoCarrito getState() {
        return state;
    }

    public List<ItemCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isActive() {
        return state == EstadoCarrito.ACTIVO;
    }

    public boolean isInCheckout() {
        return state == EstadoCarrito.EN_CHECKOUT;
    }

    public boolean isAbandoned() {
        return state == EstadoCarrito.ABANDONADO;
    }
}
