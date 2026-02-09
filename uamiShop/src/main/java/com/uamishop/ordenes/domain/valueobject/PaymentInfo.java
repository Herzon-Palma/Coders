package com.uamishop.ordenes.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;

/**
 * Value Object representing payment information within an order.
 * Implements state machine according to State_Payment.puml.
 */
public final class PaymentInfo {

    private final String method;
    private String providerRef;
    private PaymentStatus status;
    private String rejectionReason;

    public PaymentInfo(String method) {
        if (method == null || method.isBlank()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT", "Payment method is required");
        }
        this.method = method;
        this.status = PaymentStatus.PENDIENTE;
    }

    public PaymentInfo(String method, String providerRef, PaymentStatus status) {
        if (method == null || method.isBlank()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT", "Payment method is required");
        }
        this.method = method;
        this.providerRef = providerRef;
        this.status = status;
    }

    /**
     * Approves the payment with a provider reference.
     * Only allowed when status is PENDIENTE.
     */
    public void approve(String providerRef) {
        if (this.status == PaymentStatus.APROBADO) {
            // Idempotent
            return;
        }
        if (this.status != PaymentStatus.PENDIENTE) {
            throw new BusinessRuleViolation("INVALID_PAYMENT_TRANSITION",
                    "Cannot approve payment in status: " + this.status);
        }
        if (providerRef == null || providerRef.isBlank()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT", "Provider reference is required");
        }
        this.providerRef = providerRef;
        this.status = PaymentStatus.APROBADO;
    }

    /**
     * Rejects the payment with a reason.
     * Only allowed when status is PENDIENTE.
     */
    public void reject(String reason) {
        if (this.status != PaymentStatus.PENDIENTE) {
            throw new BusinessRuleViolation("INVALID_PAYMENT_TRANSITION",
                    "Cannot reject payment in status: " + this.status);
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleViolation("INVALID_PAYMENT", "Rejection reason is required");
        }
        this.rejectionReason = reason;
        this.status = PaymentStatus.RECHAZADO;
    }

    public String getMethod() {
        return method;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDIENTE;
    }

    public boolean isApproved() {
        return status == PaymentStatus.APROBADO;
    }

    public boolean isRejected() {
        return status == PaymentStatus.RECHAZADO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentInfo that = (PaymentInfo) o;
        return method.equals(that.method) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, status);
    }

    @Override
    public String toString() {
        return method + " (" + status + ")";
    }
}
