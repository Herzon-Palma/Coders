package com.uamishop.checkout.domain.event;

import com.uamishop.checkout.domain.dto.CheckoutLine;
import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.valueobject.Money;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain event emitted when an order is requested after successful payment.
 * This event triggers order creation in the Ordenes BC.
 */
public final class OrdenSolicitada implements DomainEvent {

    private final UUID checkoutId;
    private final UUID ordenId;
    private final UUID customerId;
    private final UUID carritoId;
    private final List<CheckoutLine> items;
    private final String recipientName;
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String phone;
    private final String paymentMethod;
    private final String paymentProviderRef;
    private final Money subtotal;
    private final Money discount;
    private final Money total;
    private final Instant occurredOn;

    public OrdenSolicitada(UUID checkoutId, UUID ordenId, UUID customerId, UUID carritoId,
            List<CheckoutLine> items, String recipientName, String street,
            String city, String state, String zipCode, String phone,
            String paymentMethod, String paymentProviderRef,
            Money subtotal, Money discount, Money total) {
        this.checkoutId = checkoutId;
        this.ordenId = ordenId;
        this.customerId = customerId;
        this.carritoId = carritoId;
        this.items = List.copyOf(items);
        this.recipientName = recipientName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.paymentProviderRef = paymentProviderRef;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.occurredOn = Instant.now();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public UUID getCheckoutId() {
        return checkoutId;
    }

    public UUID getOrdenId() {
        return ordenId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getCarritoId() {
        return carritoId;
    }

    public List<CheckoutLine> getItems() {
        return items;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentProviderRef() {
        return paymentProviderRef;
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
}
