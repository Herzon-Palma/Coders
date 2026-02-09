package com.uamishop.shared.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing a monetary amount with currency.
 * Immutable and validated on construction.
 */
public final class Money {

    private final BigDecimal amount;
    private final String currency;

    public static final Money ZERO_MXN = new Money(BigDecimal.ZERO, "MXN");

    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessRuleViolation("INVALID_MONEY", "Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessRuleViolation("INVALID_MONEY", "Currency cannot be null or empty");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolation("INVALID_MONEY", "Amount cannot be negative");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money pesos(double amount) {
        return new Money(BigDecimal.valueOf(amount), "MXN");
    }

    public static Money pesos(BigDecimal amount) {
        return new Money(amount, "MXN");
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolation("INVALID_MONEY", "Subtraction would result in negative amount");
        }
        return new Money(result, this.currency);
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new BusinessRuleViolation("INVALID_MONEY", "Cannot multiply by negative quantity");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new BusinessRuleViolation("CURRENCY_MISMATCH",
                    "Cannot operate on different currencies: " + this.currency + " vs " + other.currency);
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return currency + " " + amount.toPlainString();
    }
}
