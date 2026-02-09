package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import java.util.Objects;

/**
 * Value Object representing a discount applied to checkout.
 */
public final class Discount {

    private final Money amount;
    private final String reason;

    public static final Discount NONE = new Discount(Money.ZERO_MXN, "No discount");

    public Discount(Money amount, String reason) {
        if (amount == null) {
            throw new BusinessRuleViolation("INVALID_DISCOUNT", "Discount amount cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleViolation("INVALID_DISCOUNT", "Discount reason cannot be null or empty");
        }
        this.amount = amount;
        this.reason = reason;
    }

    public static Discount of(Money amount, String reason) {
        return new Discount(amount, reason);
    }

    public static Discount none() {
        return NONE;
    }

    public Money getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public boolean hasDiscount() {
        return !amount.isZero();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Discount discount = (Discount) o;
        return amount.equals(discount.amount) && reason.equals(discount.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, reason);
    }

    @Override
    public String toString() {
        return amount + " (" + reason + ")";
    }
}
