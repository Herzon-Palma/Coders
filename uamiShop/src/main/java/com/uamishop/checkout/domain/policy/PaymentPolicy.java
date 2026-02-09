package com.uamishop.checkout.domain.policy;

import com.uamishop.checkout.domain.valueobject.MetodoPago;
import com.uamishop.checkout.domain.valueobject.PaymentReceipt;
import com.uamishop.shared.domain.valueobject.Money;

/**
 * Policy interface for payment processing.
 * Port to Pagos bounded context.
 */
public interface PaymentPolicy {

    /**
     * Processes a payment.
     * 
     * @param amount the amount to charge
     * @param method the payment method
     * @return the payment receipt if successful
     * @throws PaymentRejectedException if payment is rejected
     */
    PaymentReceipt charge(Money amount, MetodoPago method);

    /**
     * Exception thrown when payment is rejected.
     */
    class PaymentRejectedException extends RuntimeException {
        private final String reason;

        public PaymentRejectedException(String reason) {
            super("Payment rejected: " + reason);
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }
}
