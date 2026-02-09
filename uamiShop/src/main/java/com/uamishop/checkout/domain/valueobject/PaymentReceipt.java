package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import java.util.Objects;

/**
 * Value Object representing a payment receipt after successful payment.
 */
public final class PaymentReceipt {

    private final MetodoPago method;
    private final String providerRef;
    private final Money amount;

    public PaymentReceipt(MetodoPago method, String providerRef, Money amount) {
        if (method == null) {
            throw new BusinessRuleViolation("INVALID_RECEIPT", "Payment method cannot be null");
        }
        if (providerRef == null || providerRef.isBlank()) {
            throw new BusinessRuleViolation("INVALID_RECEIPT", "Provider reference cannot be null or empty");
        }
        if (amount == null) {
            throw new BusinessRuleViolation("INVALID_RECEIPT", "Amount cannot be null");
        }
        this.method = method;
        this.providerRef = providerRef;
        this.amount = amount;
    }

    public MetodoPago getMethod() {
        return method;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentReceipt that = (PaymentReceipt) o;
        return method == that.method &&
                providerRef.equals(that.providerRef) &&
                amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, providerRef, amount);
    }

    @Override
    public String toString() {
        return method + " - " + providerRef + " (" + amount + ")";
    }
}
