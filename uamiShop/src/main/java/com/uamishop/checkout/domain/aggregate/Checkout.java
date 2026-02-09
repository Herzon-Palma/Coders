package com.uamishop.checkout.domain.aggregate;

import com.uamishop.checkout.domain.EstadoCheckout;
import com.uamishop.checkout.domain.dto.CheckoutLine;
import com.uamishop.checkout.domain.dto.CheckoutSummary;
import com.uamishop.checkout.domain.dto.StockLine;
import com.uamishop.checkout.domain.event.CheckoutFallido;
import com.uamishop.checkout.domain.event.CheckoutIniciado;
import com.uamishop.checkout.domain.event.CheckoutPagado;
import com.uamishop.checkout.domain.event.OrdenSolicitada;
import com.uamishop.checkout.domain.policy.CouponPolicy;
import com.uamishop.checkout.domain.policy.PaymentPolicy;
import com.uamishop.checkout.domain.policy.StockValidationPolicy;
import com.uamishop.checkout.domain.valueobject.*;
import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for the Checkout domain.
 * Orchestrates the checkout process according to State_Checkout.puml.
 * 
 * State Machine:
 * - INICIADO: Checkout started with cart summary
 * - DATOS_CAPTURADOS: Shipping and contact data captured
 * - STOCK_VALIDADO: Stock validated successfully
 * - PAGO_APROBADO: Payment approved
 * - ORDEN_CREADA: Order created (terminal success)
 * - FALLIDO: Checkout failed (terminal failure)
 * - CANCELADO: Checkout cancelled (terminal)
 */
public class Checkout {

    private final CheckoutId id;
    private final CustomerId customerId;
    private final CarritoId cartId;
    private EstadoCheckout state;
    private final CheckoutSummary summary;
    private DireccionEnvio address;
    private DatosContacto contact;
    private CodigoCupon coupon;
    private Discount discount;
    private final Money subtotal;
    private Money total;
    private PaymentReceipt payment;
    private OrderId orderId;
    private String failureReason;
    private String cancellationReason;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents;

    private Checkout(CheckoutId id, CheckoutSummary summary) {
        this.id = id;
        this.customerId = CustomerId.from(summary.getCustomerId());
        this.cartId = CarritoId.from(summary.getCartId());
        this.state = EstadoCheckout.INICIADO;
        this.summary = summary;
        this.subtotal = summary.getSubtotal();
        this.total = summary.getSubtotal();
        this.discount = Discount.none();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Starts a new checkout process with a cart summary.
     */
    public static Checkout start(CheckoutSummary summary) {
        if (summary == null) {
            throw new BusinessRuleViolation("INVALID_SUMMARY", "Checkout summary cannot be null");
        }
        if (summary.isEmpty()) {
            throw new BusinessRuleViolation("EMPTY_CART", "Cannot start checkout with empty cart");
        }

        Checkout checkout = new Checkout(CheckoutId.generate(), summary);
        checkout.domainEvents.add(new CheckoutIniciado(
                checkout.id.getValue(),
                checkout.cartId.getValue(),
                checkout.customerId.getValue()));
        return checkout;
    }

    /**
     * Captures shipping and contact data.
     * Transitions from INICIADO to DATOS_CAPTURADOS.
     */
    public void captureData(DireccionEnvio address, DatosContacto contact) {
        requireState(EstadoCheckout.INICIADO, "captureData");

        if (address == null) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "Shipping address is required");
        }
        if (contact == null) {
            throw new BusinessRuleViolation("INVALID_CONTACT", "Contact information is required");
        }

        this.address = address;
        this.contact = contact;
        this.state = EstadoCheckout.DATOS_CAPTURADOS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Applies a coupon code using the coupon policy.
     * Only allowed in DATOS_CAPTURADOS state.
     */
    public void applyCoupon(CodigoCupon couponCode, CouponPolicy couponPolicy) {
        requireState(EstadoCheckout.DATOS_CAPTURADOS, "applyCoupon");

        if (couponCode == null) {
            throw new BusinessRuleViolation("INVALID_COUPON", "Coupon code cannot be null");
        }
        if (couponPolicy == null) {
            throw new BusinessRuleViolation("INVALID_POLICY", "Coupon policy cannot be null");
        }

        try {
            Discount appliedDiscount = couponPolicy.apply(couponCode, this.subtotal);

            // Validate discount doesn't exceed subtotal
            if (appliedDiscount.getAmount().isGreaterThan(this.subtotal)) {
                throw new BusinessRuleViolation("INVALID_DISCOUNT",
                        "Discount cannot exceed subtotal");
            }

            this.coupon = couponCode;
            this.discount = appliedDiscount;
            this.total = this.subtotal.subtract(appliedDiscount.getAmount());
            this.updatedAt = LocalDateTime.now();
        } catch (CouponPolicy.CouponValidationException e) {
            throw new BusinessRuleViolation("INVALID_COUPON", e.getMessage());
        }
    }

    /**
     * Validates stock using the stock validation policy.
     * Transitions to STOCK_VALIDADO on success, FALLIDO on failure.
     */
    public void validateStock(StockValidationPolicy stockPolicy) {
        requireState(EstadoCheckout.DATOS_CAPTURADOS, "validateStock");

        if (stockPolicy == null) {
            throw new BusinessRuleViolation("INVALID_POLICY", "Stock policy cannot be null");
        }

        List<StockLine> stockLines = summary.getItems().stream()
                .map(StockLine::from)
                .toList();

        boolean stockOk = stockPolicy.validate(stockLines);

        if (stockOk) {
            this.state = EstadoCheckout.STOCK_VALIDADO;
        } else {
            this.state = EstadoCheckout.FALLIDO;
            this.failureReason = "SIN_STOCK";
            domainEvents.add(new CheckoutFallido(id.getValue(), "SIN_STOCK"));
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Processes payment using the payment policy.
     * Transitions to PAGO_APROBADO on success, FALLIDO on failure.
     */
    public void pay(MetodoPago method, PaymentPolicy paymentPolicy) {
        requireState(EstadoCheckout.STOCK_VALIDADO, "pay");

        if (method == null) {
            throw new BusinessRuleViolation("INVALID_METHOD", "Payment method is required");
        }
        if (paymentPolicy == null) {
            throw new BusinessRuleViolation("INVALID_POLICY", "Payment policy cannot be null");
        }

        try {
            PaymentReceipt receipt = paymentPolicy.charge(this.total, method);
            this.payment = receipt;
            this.state = EstadoCheckout.PAGO_APROBADO;
            domainEvents.add(new CheckoutPagado(
                    id.getValue(),
                    receipt.getProviderRef(),
                    receipt.getAmount()));
        } catch (PaymentPolicy.PaymentRejectedException e) {
            this.state = EstadoCheckout.FALLIDO;
            this.failureReason = "PAGO_RECHAZADO: " + e.getReason();
            domainEvents.add(new CheckoutFallido(id.getValue(), "PAGO_RECHAZADO"));
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates an order after successful payment.
     * Transitions to ORDEN_CREADA (idempotent if already in that state).
     */
    public OrderId createOrder() {
        if (this.state == EstadoCheckout.ORDEN_CREADA) {
            // Idempotent - return existing order ID
            return this.orderId;
        }

        requireState(EstadoCheckout.PAGO_APROBADO, "createOrder");

        if (this.payment == null) {
            throw new BusinessRuleViolation("NO_PAYMENT", "Payment receipt is required to create order");
        }

        this.orderId = OrderId.generate();
        this.state = EstadoCheckout.ORDEN_CREADA;
        this.updatedAt = LocalDateTime.now();

        domainEvents.add(new OrdenSolicitada(
                id.getValue(),
                orderId.getValue(),
                customerId.getValue(),
                cartId.getValue(),
                summary.getItems(),
                address.getRecipientName(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getPhone(),
                payment.getMethod().name(),
                payment.getProviderRef(),
                subtotal,
                discount.getAmount(),
                total));

        return this.orderId;
    }

    /**
     * Cancels the checkout process.
     * Allowed from INICIADO, DATOS_CAPTURADOS, STOCK_VALIDADO, or FALLIDO states.
     */
    public void cancel(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleViolation("INVALID_REASON", "Cancellation reason is required");
        }

        if (state == EstadoCheckout.ORDEN_CREADA) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Cannot cancel checkout after order is created");
        }
        if (state == EstadoCheckout.CANCELADO) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Checkout is already cancelled");
        }

        this.cancellationReason = reason;
        this.state = EstadoCheckout.CANCELADO;
        this.updatedAt = LocalDateTime.now();
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

    private void requireState(EstadoCheckout requiredState, String operation) {
        if (this.state != requiredState) {
            throw new BusinessRuleViolation("INVALID_STATE_TRANSITION",
                    "Operation '" + operation + "' not allowed in state " + this.state +
                            ". Required state: " + requiredState);
        }
    }

    // Getters

    public CheckoutId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public CarritoId getCartId() {
        return cartId;
    }

    public EstadoCheckout getState() {
        return state;
    }

    public CheckoutSummary getSummary() {
        return summary;
    }

    public DireccionEnvio getAddress() {
        return address;
    }

    public DatosContacto getContact() {
        return contact;
    }

    public CodigoCupon getCoupon() {
        return coupon;
    }

    public Discount getDiscount() {
        return discount;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public Money getTotal() {
        return total;
    }

    public PaymentReceipt getPayment() {
        return payment;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getFailureReason() {
        return failureReason;
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

    public boolean isFailed() {
        return state == EstadoCheckout.FALLIDO;
    }

    public boolean isCancelled() {
        return state == EstadoCheckout.CANCELADO;
    }

    public boolean isCompleted() {
        return state == EstadoCheckout.ORDEN_CREADA;
    }
}
