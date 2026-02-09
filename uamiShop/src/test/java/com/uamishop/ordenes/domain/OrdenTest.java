package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.aggregate.ItemOrden;
import com.uamishop.ordenes.domain.aggregate.Orden;
import com.uamishop.ordenes.domain.event.OrdenCancelada;
import com.uamishop.ordenes.domain.event.OrdenCreada;
import com.uamishop.ordenes.domain.event.OrdenEntregada;
import com.uamishop.ordenes.domain.event.OrdenEnviada;
import com.uamishop.ordenes.domain.valueobject.*;
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

@DisplayName("Orden Aggregate Root Tests")
class OrdenTest {

    private UsuarioId customerId;
    private List<ItemOrden> items;
    private DireccionEnvio shippingAddress;
    private Money subtotal;
    private Money discount;

    @BeforeEach
    void setUp() {
        customerId = UsuarioId.from(UUID.randomUUID());

        items = List.of(
                new ItemOrden(UUID.randomUUID(), "Laptop HP", Money.pesos(15000), 1),
                new ItemOrden(UUID.randomUUID(), "Mouse Logitech", Money.pesos(500), 2));

        shippingAddress = new DireccionEnvio(
                "Juan Pérez",
                "Calle 123",
                "Ciudad de México",
                "CDMX",
                "01234",
                "5551234567");

        subtotal = Money.pesos(16000);
        discount = Money.pesos(1000);
    }

    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create order with valid data")
        void shouldCreateOrderWithValidData() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);

            assertNotNull(orden.getId());
            assertEquals(customerId, orden.getCustomerId());
            assertEquals(OrdenStatus.PENDIENTE, orden.getStatus());
            assertEquals(2, orden.itemCount());
            assertEquals(Money.pesos(15000), orden.getTotal()); // 16000 - 1000
        }

        @Test
        @DisplayName("Should emit OrdenCreada event on creation")
        void shouldEmitOrdenCreadaEvent() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);

            List<DomainEvent> events = orden.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrdenCreada.class, events.get(0));
            OrdenCreada event = (OrdenCreada) events.get(0);
            assertEquals(orden.getId().getValue(), event.getOrdenId());
        }

        @Test
        @DisplayName("Should create order with zero discount")
        void shouldCreateOrderWithZeroDiscount() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, Money.ZERO_MXN);

            assertEquals(subtotal, orden.getTotal());
        }

        @Test
        @DisplayName("Should create order with null discount (defaults to zero)")
        void shouldCreateOrderWithNullDiscount() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, null);

            assertEquals(subtotal, orden.getTotal());
        }

        @Test
        @DisplayName("Should fail to create order with null customer ID")
        void shouldFailToCreateWithNullCustomerId() {
            assertThrows(BusinessRuleViolation.class,
                    () -> Orden.create(null, items, shippingAddress, "TARJETA", subtotal, discount));
        }

        @Test
        @DisplayName("Should fail to create order with empty items")
        void shouldFailToCreateWithEmptyItems() {
            assertThrows(BusinessRuleViolation.class,
                    () -> Orden.create(customerId, List.of(), shippingAddress, "TARJETA", subtotal, discount));
        }

        @Test
        @DisplayName("Should fail to create order with null shipping address")
        void shouldFailToCreateWithNullAddress() {
            assertThrows(BusinessRuleViolation.class,
                    () -> Orden.create(customerId, items, null, "TARJETA", subtotal, discount));
        }
    }

    @Nested
    @DisplayName("Confirmation Tests")
    class ConfirmationTests {

        @Test
        @DisplayName("Should confirm order from PENDIENTE")
        void shouldConfirmOrder() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);

            orden.confirm();

            assertEquals(OrdenStatus.CONFIRMADA, orden.getStatus());
            assertTrue(orden.isConfirmed());
            assertFalse(orden.getHistory().isEmpty());
        }

        @Test
        @DisplayName("Should fail to confirm from non-PENDIENTE state")
        void shouldFailToConfirmFromWrongState() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();

            assertThrows(BusinessRuleViolation.class, () -> orden.confirm());
        }
    }

    @Nested
    @DisplayName("Payment Tests")
    class PaymentTests {

        @Test
        @DisplayName("Should mark order as paid")
        void shouldMarkOrderAsPaid() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();

            orden.markPaid("PAY-123456");

            assertEquals(OrdenStatus.PAGADA, orden.getStatus());
            assertTrue(orden.isPaid());
            assertEquals(PaymentStatus.APROBADO, orden.getPayment().getStatus());
            assertEquals("PAY-123456", orden.getPayment().getProviderRef());
        }

        @Test
        @DisplayName("Should fail to mark paid from PENDIENTE")
        void shouldFailToMarkPaidFromPendiente() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);

            assertThrows(BusinessRuleViolation.class, () -> orden.markPaid("PAY-123"));
        }

        @Test
        @DisplayName("Should fail to mark paid with empty provider ref")
        void shouldFailToMarkPaidWithEmptyRef() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();

            assertThrows(BusinessRuleViolation.class, () -> orden.markPaid(""));
        }
    }

    @Nested
    @DisplayName("Preparation Tests")
    class PreparationTests {

        @Test
        @DisplayName("Should mark order in preparation")
        void shouldMarkInPreparation() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");

            orden.markInPreparation();

            assertEquals(OrdenStatus.EN_PREPARACION, orden.getStatus());
            assertTrue(orden.isInPreparation());
        }

        @Test
        @DisplayName("Should fail to mark in preparation before payment")
        void shouldFailToMarkInPreparationBeforePayment() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            // Skip payment

            assertThrows(BusinessRuleViolation.class, () -> orden.markInPreparation());
        }
    }

    @Nested
    @DisplayName("Shipping Tests")
    class ShippingTests {

        @Test
        @DisplayName("Should mark order as shipped")
        void shouldMarkAsShipped() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.pullDomainEvents(); // Clear previous events

            orden.markShipped("TRACK-123", "DHL");

            assertEquals(OrdenStatus.ENVIADA, orden.getStatus());
            assertTrue(orden.isShipped());
            assertNotNull(orden.getShipment());
            assertEquals("TRACK-123", orden.getShipment().getTrackingNumber());
            assertEquals("DHL", orden.getShipment().getCarrier());
        }

        @Test
        @DisplayName("Should emit OrdenEnviada event on shipping")
        void shouldEmitOrdenEnviadaEvent() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.pullDomainEvents();

            orden.markShipped("TRACK-123", "DHL");

            List<DomainEvent> events = orden.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrdenEnviada.class, events.get(0));
        }

        @Test
        @DisplayName("Should fail to ship twice")
        void shouldFailToShipTwice() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.markShipped("TRACK-456", "FEDEX"));
        }

        @Test
        @DisplayName("Should fail to ship before preparation")
        void shouldFailToShipBeforePreparation() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            // Skip preparation

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.markShipped("TRACK-123", "DHL"));
        }
    }

    @Nested
    @DisplayName("Delivery Tests")
    class DeliveryTests {

        @Test
        @DisplayName("Should mark order as delivered")
        void shouldMarkAsDelivered() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");
            orden.pullDomainEvents();

            orden.markDelivered();

            assertEquals(OrdenStatus.ENTREGADA, orden.getStatus());
            assertTrue(orden.isDelivered());
        }

        @Test
        @DisplayName("Should emit OrdenEntregada event on delivery")
        void shouldEmitOrdenEntregadaEvent() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");
            orden.pullDomainEvents();

            orden.markDelivered();

            List<DomainEvent> events = orden.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrdenEntregada.class, events.get(0));
        }

        @Test
        @DisplayName("Should fail to deliver before shipping")
        void shouldFailToDeliverBeforeShipping() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            // Skip shipping

            assertThrows(BusinessRuleViolation.class, () -> orden.markDelivered());
        }
    }

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should cancel from PENDIENTE state")
        void shouldCancelFromPendiente() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.pullDomainEvents();

            orden.cancel("Customer request");

            assertEquals(OrdenStatus.CANCELADA, orden.getStatus());
            assertTrue(orden.isCancelled());
            assertEquals("Customer request", orden.getCancellationReason());
        }

        @Test
        @DisplayName("Should emit OrdenCancelada event on cancellation")
        void shouldEmitOrdenCanceladaEvent() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.pullDomainEvents();

            orden.cancel("Customer request");

            List<DomainEvent> events = orden.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrdenCancelada.class, events.get(0));
        }

        @Test
        @DisplayName("Should cancel from CONFIRMADA state (payment pending)")
        void shouldCancelFromConfirmada() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();

            orden.cancel("Out of stock");

            assertEquals(OrdenStatus.CANCELADA, orden.getStatus());
        }

        @Test
        @DisplayName("Should fail to cancel after PAGADA")
        void shouldFailToCancelAfterPagada() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel("Too late"));
        }

        @Test
        @DisplayName("Should fail to cancel after EN_PREPARACION")
        void shouldFailToCancelAfterEnPreparacion() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel("Cannot cancel"));
        }

        @Test
        @DisplayName("Should fail to cancel after ENVIADA")
        void shouldFailToCancelAfterEnviada() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel("Want refund"));
        }

        @Test
        @DisplayName("Should fail to cancel after ENTREGADA")
        void shouldFailToCancelAfterEntregada() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");
            orden.markDelivered();

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel("Don't want it"));
        }

        @Test
        @DisplayName("Should fail to cancel with empty reason")
        void shouldFailToCancelWithEmptyReason() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel(""));
        }

        @Test
        @DisplayName("Should fail to cancel already cancelled order")
        void shouldFailToCancelAlreadyCancelled() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.cancel("First cancel");

            assertThrows(BusinessRuleViolation.class,
                    () -> orden.cancel("Second cancel"));
        }
    }

    @Nested
    @DisplayName("Status History Tests")
    class StatusHistoryTests {

        @Test
        @DisplayName("Should track status history through lifecycle")
        void shouldTrackStatusHistory() {
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            orden.confirm();
            orden.markPaid("PAY-123");
            orden.markInPreparation();
            orden.markShipped("TRACK-123", "DHL");
            orden.markDelivered();

            List<StatusChange> history = orden.getHistory();

            assertEquals(5, history.size());
            assertEquals(OrdenStatus.PENDIENTE, history.get(0).getFrom());
            assertEquals(OrdenStatus.CONFIRMADA, history.get(0).getTo());
            assertEquals(OrdenStatus.ENVIADA, history.get(4).getFrom());
            assertEquals(OrdenStatus.ENTREGADA, history.get(4).getTo());
        }
    }

    @Nested
    @DisplayName("Full Order Lifecycle Test")
    class FullOrderLifecycleTest {

        @Test
        @DisplayName("Should complete full order lifecycle")
        void shouldCompleteFullLifecycle() {
            // Create
            Orden orden = Orden.create(customerId, items, shippingAddress, "TARJETA", subtotal, discount);
            assertEquals(OrdenStatus.PENDIENTE, orden.getStatus());

            // Confirm
            orden.confirm();
            assertEquals(OrdenStatus.CONFIRMADA, orden.getStatus());

            // Pay
            orden.markPaid("PAY-123");
            assertEquals(OrdenStatus.PAGADA, orden.getStatus());
            assertEquals(PaymentStatus.APROBADO, orden.getPayment().getStatus());

            // Prepare
            orden.markInPreparation();
            assertEquals(OrdenStatus.EN_PREPARACION, orden.getStatus());

            // Ship
            orden.markShipped("TRACK-123", "DHL");
            assertEquals(OrdenStatus.ENVIADA, orden.getStatus());
            assertNotNull(orden.getShipment());

            // Deliver
            orden.markDelivered();
            assertEquals(OrdenStatus.ENTREGADA, orden.getStatus());
            assertTrue(orden.isDelivered());

            // Verify history
            assertEquals(5, orden.getHistory().size());
        }
    }
}
