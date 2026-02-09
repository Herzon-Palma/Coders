package com.uamishop.ordenes.domain.aggregate;

import com.uamishop.ordenes.domain.event.OrdenCancelada;
import com.uamishop.ordenes.domain.event.OrdenCreada;
import com.uamishop.ordenes.domain.event.OrdenEntregada;
import com.uamishop.ordenes.domain.event.OrdenEnviada;
import com.uamishop.ordenes.domain.valueobject.*;
import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root for the Order domain.
 * Manages order lifecycle according to State_Orden.puml and State_Payment.puml.
 * 
 * Order State Machine:
 * - PENDIENTE: Initial state after creation
 * - CONFIRMADA: Order confirmed
 * - PAGADA: Payment approved
 * - EN_PREPARACION: Being prepared for shipment
 * - ENVIADA: Shipped with tracking info
 * - ENTREGADA: Delivered (terminal success)
 * - CANCELADA: Cancelled (terminal failure)
 * 
 * Cancellation Rules:
 * - Can cancel from PENDIENTE or CONFIRMADA (if payment not approved)
 * - Cannot cancel after PAGADA
 */
public class Orden {

    private final OrdenId id;
    private final UsuarioId customerId;
    private final List<ItemOrden> items;
    private final DireccionEnvio shippingAddress;
    private final PaymentInfo payment;
    private final Money subtotal;
    private final Money discount;
    private Money total;
    private OrdenStatus status;
    private final List<StatusChange> history;
    private ShipmentInfo shipment;
    private String cancellationReason;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents;

    private Orden(OrdenId id, UsuarioId customerId, List<ItemOrden> items,
            DireccionEnvio shippingAddress, String paymentMethod,
            Money subtotal, Money discount) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.shippingAddress = shippingAddress;
        this.payment = new PaymentInfo(paymentMethod);
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = subtotal.subtract(discount);
        this.status = OrdenStatus.PENDIENTE;
        this.history = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Factory method to create a new order.
     */
    public static Orden create(UsuarioId customerId, List<ItemOrden> items,
            DireccionEnvio shippingAddress, String paymentMethod,
            Money subtotal, Money discount) {
        if (customerId == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Shipping address is required");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Payment method is required");
        }
        if (subtotal == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Subtotal is required");
        }

        Money actualDiscount = discount != null ? discount : Money.ZERO_MXN;

        Orden orden = new Orden(
                OrdenId.generate(),
                customerId,
                items,
                shippingAddress,
                paymentMethod,
                subtotal,
                actualDiscount);

        orden.domainEvents.add(new OrdenCreada(
                orden.id.getValue(),
                orden.customerId.getValue(),
                orden.total));

        return orden;
    }

    /**
     * Factory method with explicit order ID (for reconstitution from event).
     */
    public static Orden createWithId(OrdenId ordenId, UsuarioId customerId, List<ItemOrden> items,
            DireccionEnvio shippingAddress, String paymentMethod,
            Money subtotal, Money discount) {
        if (ordenId == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Order ID is required");
        }
        if (customerId == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new BusinessRuleViolation("INVALID_ORDER", "Shipping address is required");
        }

        Money actualDiscount = discount != null ? discount : Money.ZERO_MXN;

        Orden orden = new Orden(
                ordenId,
                customerId,
                items,
                shippingAddress,
                paymentMethod != null ? paymentMethod : "UNKNOWN",
                subtotal,
                actualDiscount);

        orden.domainEvents.add(new OrdenCreada(
                orden.id.getValue(),
                orden.customerId.getValue(),
                orden.total));

        return orden;
    }

    /**
     * Confirms the order.
     * Transitions from PENDIENTE to CONFIRMADA.
     */
    public void confirm() {
        requireStatus(OrdenStatus.PENDIENTE, "confirm");
        transitionTo(OrdenStatus.CONFIRMADA, "Order confirmed");
    }

    /**
     * Marks the order as paid.
     * Transitions from CONFIRMADA to PAGADA.
     */
    public void markPaid(String providerRef) {
        requireStatus(OrdenStatus.CONFIRMADA, "markPaid");

        if (!payment.isPending()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT_STATE",
                    "Payment must be in PENDIENTE status to mark as paid");
        }

        payment.approve(providerRef);
        transitionTo(OrdenStatus.PAGADA, "Payment approved: " + providerRef);
    }

    /**
     * Marks the order as in preparation.
     * Transitions from PAGADA to EN_PREPARACION.
     */
    public void markInPreparation() {
        requireStatus(OrdenStatus.PAGADA, "markInPreparation");

        if (!payment.isApproved()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT_STATE",
                    "Payment must be approved before preparation");
        }

        transitionTo(OrdenStatus.EN_PREPARACION, "Order in preparation");
    }

    /**
     * Marks the order as shipped.
     * Transitions from EN_PREPARACION to ENVIADA.
     */
    public void markShipped(String trackingNumber, String carrier) {
        requireStatus(OrdenStatus.EN_PREPARACION, "markShipped");

        if (this.shipment != null) {
            throw new BusinessRuleViolation("ALREADY_SHIPPED",
                    "Order already has shipment info");
        }

        this.shipment = new ShipmentInfo(trackingNumber, carrier);
        transitionTo(OrdenStatus.ENVIADA, "Shipped via " + carrier + ": " + trackingNumber);

        domainEvents.add(new OrdenEnviada(id.getValue(), trackingNumber, carrier));
    }

    /**
     * Marks the order as delivered.
     * Transitions from ENVIADA to ENTREGADA.
     */
    public void markDelivered() {
        requireStatus(OrdenStatus.ENVIADA, "markDelivered");

        if (this.shipment == null) {
            throw new BusinessRuleViolation("NO_SHIPMENT",
                    "Order must have shipment info before delivery");
        }

        transitionTo(OrdenStatus.ENTREGADA, "Order delivered");
        domainEvents.add(new OrdenEntregada(id.getValue()));
    }

    /**
     * Cancels the order.
     * Only allowed from PENDIENTE or CONFIRMADA (if payment not approved).
     */
    public void cancel(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleViolation("INVALID_REASON", "Cancellation reason is required");
        }

        // Check if cancellation is allowed
        if (status == OrdenStatus.CANCELADA) {
            throw new BusinessRuleViolation("ALREADY_CANCELLED", "Order is already cancelled");
        }

        if (status == OrdenStatus.ENTREGADA) {
            throw new BusinessRuleViolation("CANNOT_CANCEL",
                    "Cannot cancel a delivered order");
        }

        // Cannot cancel after PAGADA
        if (status == OrdenStatus.PAGADA ||
                status == OrdenStatus.EN_PREPARACION ||
                status == OrdenStatus.ENVIADA) {
            throw new BusinessRuleViolation("CANNOT_CANCEL",
                    "Cannot cancel order after payment. Status: " + status);
        }

        // For CONFIRMADA, check if payment is not approved
        if (status == OrdenStatus.CONFIRMADA && payment.isApproved()) {
            throw new BusinessRuleViolation("CANNOT_CANCEL",
                    "Cannot cancel order with approved payment");
        }

        this.cancellationReason = reason;
        transitionTo(OrdenStatus.CANCELADA, "Cancelled: " + reason);
        domainEvents.add(new OrdenCancelada(id.getValue(), reason));
    }

    /**
     * Recalculates totals based on items.
     */
    public void recalculateTotals() {
        Money calculatedSubtotal = items.stream()
                .map(ItemOrden::lineTotal)
                .reduce(Money::add)
                .orElse(Money.ZERO_MXN);

        this.total = calculatedSubtotal.subtract(this.discount);
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

    private void requireStatus(OrdenStatus requiredStatus, String operation) {
        if (this.status != requiredStatus) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Operation '" + operation + "' not allowed in status " + this.status +
                            ". Required status: " + requiredStatus);
        }
    }

    private void transitionTo(OrdenStatus newStatus, String note) {
        history.add(new StatusChange(this.status, newStatus, note));
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public OrdenId getId() {
        return id;
    }

    public UsuarioId getCustomerId() {
        return customerId;
    }

    public List<ItemOrden> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DireccionEnvio getShippingAddress() {
        return shippingAddress;
    }

    public PaymentInfo getPayment() {
        return payment;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public Money getDiscount() {
        return discount;
    }

    public Money getTotal() {
        return total;
    }

    public OrdenStatus getStatus() {
        return status;
    }

    public List<StatusChange> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public ShipmentInfo getShipment() {
        return shipment;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPending() {
        return status == OrdenStatus.PENDIENTE;
    }

    public boolean isConfirmed() {
        return status == OrdenStatus.CONFIRMADA;
    }

    public boolean isPaid() {
        return status == OrdenStatus.PAGADA;
    }

    public boolean isInPreparation() {
        return status == OrdenStatus.EN_PREPARACION;
    }

    public boolean isShipped() {
        return status == OrdenStatus.ENVIADA;
    }

    public boolean isDelivered() {
        return status == OrdenStatus.ENTREGADA;
    }

    public boolean isCancelled() {
        return status == OrdenStatus.CANCELADA;
    }

    public int itemCount() {
        return items.size();
    }
}
