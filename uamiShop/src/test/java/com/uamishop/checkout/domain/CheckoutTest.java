package com.uamishop.checkout.domain;

import com.uamishop.checkout.domain.aggregate.Checkout;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checkout Aggregate Root Tests")
class CheckoutTest {

    private CheckoutSummary validSummary;
    private DireccionEnvio validAddress;
    private DatosContacto validContact;

    // Test stubs for policies
    private StockValidationPolicy stockOkPolicy;
    private StockValidationPolicy stockFailPolicy;
    private CouponPolicy validCouponPolicy;
    private CouponPolicy invalidCouponPolicy;
    private PaymentPolicy paymentApprovedPolicy;
    private PaymentPolicy paymentRejectedPolicy;

    @BeforeEach
    void setUp() {
        // Create valid checkout summary
        List<CheckoutLine> items = List.of(
                new CheckoutLine(UUID.randomUUID(), "Laptop HP", Money.pesos(15000), 1),
                new CheckoutLine(UUID.randomUUID(), "Mouse Logitech", Money.pesos(500), 2));
        validSummary = new CheckoutSummary(
                UUID.randomUUID(),
                UUID.randomUUID(),
                items,
                Money.pesos(16000));

        validAddress = new DireccionEnvio(
                "Juan Pérez",
                "Calle 123",
                "Ciudad de México",
                "CDMX",
                "01234",
                "5551234567");

        validContact = new DatosContacto("juan@example.com", "5551234567");

        // Setup policy stubs
        stockOkPolicy = stockLines -> true;
        stockFailPolicy = stockLines -> false;

        validCouponPolicy = (coupon, subtotal) -> Discount.of(Money.pesos(1000), "10% discount");

        invalidCouponPolicy = (coupon, subtotal) -> {
            throw new CouponPolicy.CouponValidationException("Invalid coupon");
        };

        paymentApprovedPolicy = (amount, method) -> new PaymentReceipt(method, "PAY-" + UUID.randomUUID(), amount);

        paymentRejectedPolicy = (amount, method) -> {
            throw new PaymentPolicy.PaymentRejectedException("Insufficient funds");
        };
    }

    @Nested
    @DisplayName("Start Checkout Tests")
    class StartCheckoutTests {

        @Test
        @DisplayName("Should start checkout with valid summary")
        void shouldStartCheckoutWithValidSummary() {
            Checkout checkout = Checkout.start(validSummary);

            assertNotNull(checkout.getId());
            assertEquals(EstadoCheckout.INICIADO, checkout.getState());
            assertEquals(validSummary.getCartId(), checkout.getCartId().getValue());
            assertEquals(validSummary.getCustomerId(), checkout.getCustomerId().getValue());
        }

        @Test
        @DisplayName("Should emit CheckoutIniciado event on start")
        void shouldEmitCheckoutIniciadoEvent() {
            Checkout checkout = Checkout.start(validSummary);

            List<DomainEvent> events = checkout.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(CheckoutIniciado.class, events.get(0));
        }

        @Test
        @DisplayName("Should fail to start with null summary")
        void shouldFailToStartWithNullSummary() {
            assertThrows(BusinessRuleViolation.class, () -> Checkout.start(null));
        }

        @Test
        @DisplayName("Should fail to start with empty cart")
        void shouldFailToStartWithEmptyCart() {
            CheckoutSummary emptySummary = new CheckoutSummary(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    List.of(),
                    Money.ZERO_MXN);

            assertThrows(BusinessRuleViolation.class, () -> Checkout.start(emptySummary));
        }
    }

    @Nested
    @DisplayName("Capture Data Tests")
    class CaptureDataTests {

        @Test
        @DisplayName("Should capture shipping data successfully")
        void shouldCaptureShippingData() {
            Checkout checkout = Checkout.start(validSummary);

            checkout.captureData(validAddress, validContact);

            assertEquals(EstadoCheckout.DATOS_CAPTURADOS, checkout.getState());
            assertEquals(validAddress, checkout.getAddress());
            assertEquals(validContact, checkout.getContact());
        }

        @Test
        @DisplayName("Should fail to capture data with null address")
        void shouldFailToCaptureDataWithNullAddress() {
            Checkout checkout = Checkout.start(validSummary);

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.captureData(null, validContact));
        }

        @Test
        @DisplayName("Should fail to capture data with null contact")
        void shouldFailToCaptureDataWithNullContact() {
            Checkout checkout = Checkout.start(validSummary);

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.captureData(validAddress, null));
        }

        @Test
        @DisplayName("Should fail to capture data in wrong state")
        void shouldFailToCaptureDataInWrongState() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact); // Now in DATOS_CAPTURADOS

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.captureData(validAddress, validContact));
        }
    }

    @Nested
    @DisplayName("Apply Coupon Tests")
    class ApplyCouponTests {

        @Test
        @DisplayName("Should apply valid coupon")
        void shouldApplyValidCoupon() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);

            CodigoCupon coupon = new CodigoCupon("DESCUENTO10");
            checkout.applyCoupon(coupon, validCouponPolicy);

            assertTrue(checkout.getDiscount().hasDiscount());
            assertEquals(Money.pesos(1000), checkout.getDiscount().getAmount());
            assertEquals(Money.pesos(15000), checkout.getTotal()); // 16000 - 1000
        }

        @Test
        @DisplayName("Should fail with invalid coupon")
        void shouldFailWithInvalidCoupon() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);

            CodigoCupon coupon = new CodigoCupon("INVALID");

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.applyCoupon(coupon, invalidCouponPolicy));
        }

        @Test
        @DisplayName("Should fail to apply coupon in wrong state")
        void shouldFailToApplyCouponInWrongState() {
            Checkout checkout = Checkout.start(validSummary);
            // Still in INICIADO

            CodigoCupon coupon = new CodigoCupon("DESCUENTO10");

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.applyCoupon(coupon, validCouponPolicy));
        }
    }

    @Nested
    @DisplayName("Validate Stock Tests")
    class ValidateStockTests {

        @Test
        @DisplayName("Should validate stock successfully")
        void shouldValidateStockSuccessfully() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);

            checkout.validateStock(stockOkPolicy);

            assertEquals(EstadoCheckout.STOCK_VALIDADO, checkout.getState());
        }

        @Test
        @DisplayName("Should transition to FALLIDO when stock fails")
        void shouldTransitionToFallidoWhenStockFails() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.pullDomainEvents(); // Clear previous events

            checkout.validateStock(stockFailPolicy);

            assertEquals(EstadoCheckout.FALLIDO, checkout.getState());
            assertTrue(checkout.isFailed());
            assertEquals("SIN_STOCK", checkout.getFailureReason());
        }

        @Test
        @DisplayName("Should emit CheckoutFallido event when stock fails")
        void shouldEmitFallidoEventWhenStockFails() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.pullDomainEvents(); // Clear previous events

            checkout.validateStock(stockFailPolicy);

            List<DomainEvent> events = checkout.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(CheckoutFallido.class, events.get(0));
        }
    }

    @Nested
    @DisplayName("Payment Tests")
    class PaymentTests {

        @Test
        @DisplayName("Should process payment successfully")
        void shouldProcessPaymentSuccessfully() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pullDomainEvents(); // Clear previous events

            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);

            assertEquals(EstadoCheckout.PAGO_APROBADO, checkout.getState());
            assertNotNull(checkout.getPayment());
            assertEquals(MetodoPago.TARJETA, checkout.getPayment().getMethod());
        }

        @Test
        @DisplayName("Should emit CheckoutPagado event on successful payment")
        void shouldEmitCheckoutPagadoEvent() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pullDomainEvents();

            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);

            List<DomainEvent> events = checkout.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(CheckoutPagado.class, events.get(0));
        }

        @Test
        @DisplayName("Should transition to FALLIDO when payment is rejected")
        void shouldTransitionToFallidoWhenPaymentRejected() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pullDomainEvents();

            checkout.pay(MetodoPago.TARJETA, paymentRejectedPolicy);

            assertEquals(EstadoCheckout.FALLIDO, checkout.getState());
            assertTrue(checkout.getFailureReason().contains("PAGO_RECHAZADO"));
        }

        @Test
        @DisplayName("Should fail to pay in wrong state")
        void shouldFailToPayInWrongState() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            // Skip stock validation

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy));
        }
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order after payment")
        void shouldCreateOrderAfterPayment() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);
            checkout.pullDomainEvents();

            OrderId orderId = checkout.createOrder();

            assertNotNull(orderId);
            assertEquals(EstadoCheckout.ORDEN_CREADA, checkout.getState());
            assertTrue(checkout.isCompleted());
        }

        @Test
        @DisplayName("Should emit OrdenSolicitada event on createOrder")
        void shouldEmitOrdenSolicitadaEvent() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);
            checkout.pullDomainEvents();

            checkout.createOrder();

            List<DomainEvent> events = checkout.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrdenSolicitada.class, events.get(0));
        }

        @Test
        @DisplayName("Should be idempotent - return same orderId on multiple calls")
        void shouldBeIdempotent() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);

            OrderId orderId1 = checkout.createOrder();
            OrderId orderId2 = checkout.createOrder();

            assertEquals(orderId1, orderId2);
        }

        @Test
        @DisplayName("Should fail to create order before payment")
        void shouldFailToCreateOrderBeforePayment() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            // Skip payment

            assertThrows(BusinessRuleViolation.class, () -> checkout.createOrder());
        }
    }

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should cancel from INICIADO state")
        void shouldCancelFromIniciado() {
            Checkout checkout = Checkout.start(validSummary);

            checkout.cancel("Changed my mind");

            assertEquals(EstadoCheckout.CANCELADO, checkout.getState());
            assertTrue(checkout.isCancelled());
        }

        @Test
        @DisplayName("Should cancel from DATOS_CAPTURADOS state")
        void shouldCancelFromDatosCapturados() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);

            checkout.cancel("Found better price");

            assertEquals(EstadoCheckout.CANCELADO, checkout.getState());
        }

        @Test
        @DisplayName("Should cancel from STOCK_VALIDADO state")
        void shouldCancelFromStockValidado() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);

            checkout.cancel("Don't need it anymore");

            assertEquals(EstadoCheckout.CANCELADO, checkout.getState());
        }

        @Test
        @DisplayName("Should cancel from FALLIDO state")
        void shouldCancelFromFallido() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockFailPolicy); // Transitions to FALLIDO

            checkout.cancel("Giving up");

            assertEquals(EstadoCheckout.CANCELADO, checkout.getState());
        }

        @Test
        @DisplayName("Should fail to cancel after order created")
        void shouldFailToCancelAfterOrderCreated() {
            Checkout checkout = Checkout.start(validSummary);
            checkout.captureData(validAddress, validContact);
            checkout.validateStock(stockOkPolicy);
            checkout.pay(MetodoPago.TARJETA, paymentApprovedPolicy);
            checkout.createOrder();

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.cancel("Too late"));
        }

        @Test
        @DisplayName("Should fail to cancel with empty reason")
        void shouldFailToCancelWithEmptyReason() {
            Checkout checkout = Checkout.start(validSummary);

            assertThrows(BusinessRuleViolation.class,
                    () -> checkout.cancel(""));
        }
    }

    @Nested
    @DisplayName("Full Checkout Flow Test")
    class FullCheckoutFlowTest {

        @Test
        @DisplayName("Should complete full checkout flow successfully")
        void shouldCompleteFullCheckoutFlow() {
            // Start
            Checkout checkout = Checkout.start(validSummary);
            assertEquals(EstadoCheckout.INICIADO, checkout.getState());

            // Capture data
            checkout.captureData(validAddress, validContact);
            assertEquals(EstadoCheckout.DATOS_CAPTURADOS, checkout.getState());

            // Apply coupon (optional)
            checkout.applyCoupon(new CodigoCupon("DESCUENTO10"), validCouponPolicy);
            assertEquals(Money.pesos(15000), checkout.getTotal());

            // Validate stock
            checkout.validateStock(stockOkPolicy);
            assertEquals(EstadoCheckout.STOCK_VALIDADO, checkout.getState());

            // Pay
            checkout.pay(MetodoPago.TRANSFERENCIA, paymentApprovedPolicy);
            assertEquals(EstadoCheckout.PAGO_APROBADO, checkout.getState());

            // Create order
            OrderId orderId = checkout.createOrder();
            assertNotNull(orderId);
            assertEquals(EstadoCheckout.ORDEN_CREADA, checkout.getState());
            assertTrue(checkout.isCompleted());
        }
    }
}
