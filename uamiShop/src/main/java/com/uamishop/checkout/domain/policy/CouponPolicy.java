package com.uamishop.checkout.domain.policy;

import com.uamishop.checkout.domain.valueobject.CodigoCupon;
import com.uamishop.checkout.domain.valueobject.Discount;
import com.uamishop.shared.domain.valueobject.Money;

/**
 * Policy interface for coupon validation and application.
 * Port to Promociones bounded context.
 */
public interface CouponPolicy {

    /**
     * Validates and applies a coupon code.
     * 
     * @param coupon   the coupon code to apply
     * @param subtotal the current subtotal
     * @return the discount to apply, or Discount.NONE if invalid
     * @throws CouponValidationException if the coupon is invalid
     */
    Discount apply(CodigoCupon coupon, Money subtotal);

    /**
     * Exception thrown when coupon validation fails.
     */
    class CouponValidationException extends RuntimeException {
        public CouponValidationException(String message) {
            super(message);
        }
    }
}
