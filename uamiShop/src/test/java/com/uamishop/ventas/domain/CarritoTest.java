package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.event.DomainEvent;
import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import com.uamishop.shared.domain.valueobject.Money;
import com.uamishop.ventas.domain.aggregate.Carrito;
import com.uamishop.ventas.domain.aggregate.ItemCarrito;
import com.uamishop.ventas.domain.event.CarritoConfirmadoParaCheckout;
import com.uamishop.ventas.domain.event.ItemAgregadoAlCarrito;
import com.uamishop.ventas.domain.event.ItemEliminadoDelCarrito;
import com.uamishop.ventas.domain.valueobject.ProductoRef;
import com.uamishop.ventas.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Carrito Aggregate Root Tests")
class CarritoTest {

    private UsuarioId customerId;
    private ProductoRef producto1;
    private ProductoRef producto2;

    @BeforeEach
    void setUp() {
        customerId = UsuarioId.generate();
        producto1 = new ProductoRef(UUID.randomUUID(), "Laptop HP", Money.pesos(15000));
        producto2 = new ProductoRef(UUID.randomUUID(), "Mouse Logitech", Money.pesos(500));
    }

    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create cart with valid customer ID")
        void shouldCreateCartWithValidCustomerId() {
            Carrito carrito = Carrito.create(customerId);

            assertNotNull(carrito.getId());
            assertEquals(customerId, carrito.getCustomerId());
            assertEquals(EstadoCarrito.ACTIVO, carrito.getState());
            assertTrue(carrito.isEmpty());
            assertEquals(0, carrito.totalItems());
        }

        @Test
        @DisplayName("Should fail to create cart with null customer ID")
        void shouldFailToCreateCartWithNullCustomerId() {
            assertThrows(BusinessRuleViolation.class, () -> Carrito.create(null));
        }
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        @Test
        @DisplayName("Should add item to empty cart")
        void shouldAddItemToEmptyCart() {
            Carrito carrito = Carrito.create(customerId);

            carrito.addItem(producto1, 2);

            assertEquals(1, carrito.distinctProducts());
            assertEquals(2, carrito.totalItems());
            assertTrue(carrito.containsProduct(producto1.getProductId()));
        }

        @Test
        @DisplayName("Should increase quantity when adding existing product")
        void shouldIncreaseQuantityWhenAddingExistingProduct() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);

            carrito.addItem(producto1, 3);

            assertEquals(1, carrito.distinctProducts());
            assertEquals(5, carrito.totalItems());
        }

        @Test
        @DisplayName("Should add multiple different products")
        void shouldAddMultipleDifferentProducts() {
            Carrito carrito = Carrito.create(customerId);

            carrito.addItem(producto1, 1);
            carrito.addItem(producto2, 2);

            assertEquals(2, carrito.distinctProducts());
            assertEquals(3, carrito.totalItems());
        }

        @Test
        @DisplayName("Should emit ItemAgregadoAlCarrito event")
        void shouldEmitItemAgregadoEvent() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);

            List<DomainEvent> events = carrito.pullDomainEvents();

            assertEquals(1, events.size());
            assertInstanceOf(ItemAgregadoAlCarrito.class, events.get(0));
            ItemAgregadoAlCarrito event = (ItemAgregadoAlCarrito) events.get(0);
            assertEquals(producto1.getProductId(), event.getProductoId());
            assertEquals(2, event.getCantidad());
        }

        @Test
        @DisplayName("Should fail to add item with zero quantity")
        void shouldFailToAddItemWithZeroQuantity() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class, () -> carrito.addItem(producto1, 0));
        }

        @Test
        @DisplayName("Should fail to add item with negative quantity")
        void shouldFailToAddItemWithNegativeQuantity() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class, () -> carrito.addItem(producto1, -1));
        }

        @Test
        @DisplayName("Should fail to add null product")
        void shouldFailToAddNullProduct() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class, () -> carrito.addItem(null, 1));
        }
    }

    @Nested
    @DisplayName("Update Quantity Tests")
    class UpdateQuantityTests {

        @Test
        @DisplayName("Should update quantity of existing item")
        void shouldUpdateQuantityOfExistingItem() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);

            carrito.updateQty(producto1.getProductId(), 5);

            assertEquals(5, carrito.totalItems());
        }

        @Test
        @DisplayName("Should fail to update non-existent item")
        void shouldFailToUpdateNonExistentItem() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.updateQty(UUID.randomUUID(), 5));
        }

        @Test
        @DisplayName("Should fail to update with zero quantity")
        void shouldFailToUpdateWithZeroQuantity() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.updateQty(producto1.getProductId(), 0));
        }
    }

    @Nested
    @DisplayName("Remove Item Tests")
    class RemoveItemTests {

        @Test
        @DisplayName("Should remove existing item")
        void shouldRemoveExistingItem() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);
            carrito.addItem(producto2, 1);
            carrito.pullDomainEvents(); // Clear events

            carrito.removeItem(producto1.getProductId());

            assertEquals(1, carrito.distinctProducts());
            assertFalse(carrito.containsProduct(producto1.getProductId()));
            assertTrue(carrito.containsProduct(producto2.getProductId()));
        }

        @Test
        @DisplayName("Should emit ItemEliminadoDelCarrito event")
        void shouldEmitItemEliminadoEvent() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);
            carrito.pullDomainEvents(); // Clear add events

            carrito.removeItem(producto1.getProductId());

            List<DomainEvent> events = carrito.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(ItemEliminadoDelCarrito.class, events.get(0));
        }

        @Test
        @DisplayName("Should fail to remove non-existent item")
        void shouldFailToRemoveNonExistentItem() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.removeItem(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("Clear Cart Tests")
    class ClearCartTests {

        @Test
        @DisplayName("Should clear all items from cart")
        void shouldClearAllItems() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);
            carrito.addItem(producto2, 1);

            carrito.clear();

            assertTrue(carrito.isEmpty());
            assertEquals(0, carrito.distinctProducts());
        }
    }

    @Nested
    @DisplayName("Subtotal Calculation Tests")
    class SubtotalCalculationTests {

        @Test
        @DisplayName("Should calculate subtotal correctly")
        void shouldCalculateSubtotalCorrectly() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2); // 15000 * 2 = 30000
            carrito.addItem(producto2, 3); // 500 * 3 = 1500

            Money subtotal = carrito.subtotal();

            assertEquals(Money.pesos(31500), subtotal);
        }

        @Test
        @DisplayName("Should return zero for empty cart")
        void shouldReturnZeroForEmptyCart() {
            Carrito carrito = Carrito.create(customerId);

            Money subtotal = carrito.subtotal();

            assertTrue(subtotal.isZero());
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should transition from ACTIVO to EN_CHECKOUT on startCheckout")
        void shouldTransitionToEnCheckout() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.pullDomainEvents(); // Clear add events

            carrito.startCheckout();

            assertEquals(EstadoCarrito.EN_CHECKOUT, carrito.getState());
            assertTrue(carrito.isInCheckout());
        }

        @Test
        @DisplayName("Should emit CarritoConfirmadoParaCheckout event on startCheckout")
        void shouldEmitCheckoutEvent() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 2);
            carrito.pullDomainEvents(); // Clear add events

            carrito.startCheckout();

            List<DomainEvent> events = carrito.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(CarritoConfirmadoParaCheckout.class, events.get(0));
            CarritoConfirmadoParaCheckout event = (CarritoConfirmadoParaCheckout) events.get(0);
            assertEquals(2, event.getTotalItems());
        }

        @Test
        @DisplayName("Should fail startCheckout with empty cart")
        void shouldFailStartCheckoutWithEmptyCart() {
            Carrito carrito = Carrito.create(customerId);

            assertThrows(BusinessRuleViolation.class, () -> carrito.startCheckout());
        }

        @Test
        @DisplayName("Should transition to ABANDONADO on completeCheckout")
        void shouldTransitionToAbandonadoOnComplete() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.startCheckout();

            carrito.completeCheckout();

            assertEquals(EstadoCarrito.ABANDONADO, carrito.getState());
            assertTrue(carrito.isAbandoned());
        }

        @Test
        @DisplayName("Should transition to ABANDONADO on abandon from ACTIVO")
        void shouldAbandonFromActivo() {
            Carrito carrito = Carrito.create(customerId);

            carrito.abandon();

            assertEquals(EstadoCarrito.ABANDONADO, carrito.getState());
        }

        @Test
        @DisplayName("Should transition to ABANDONADO on abandon from EN_CHECKOUT")
        void shouldAbandonFromEnCheckout() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.startCheckout();

            carrito.abandon();

            assertEquals(EstadoCarrito.ABANDONADO, carrito.getState());
        }

        @Test
        @DisplayName("Should fail to abandon already abandoned cart")
        void shouldFailToAbandonAlreadyAbandonedCart() {
            Carrito carrito = Carrito.create(customerId);
            carrito.abandon();

            assertThrows(BusinessRuleViolation.class, () -> carrito.abandon());
        }
    }

    @Nested
    @DisplayName("Invalid State Operations Tests")
    class InvalidStateOperationsTests {

        @Test
        @DisplayName("Should fail to add item in EN_CHECKOUT state")
        void shouldFailToAddItemInCheckoutState() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.startCheckout();

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.addItem(producto2, 1));
        }

        @Test
        @DisplayName("Should fail to remove item in EN_CHECKOUT state")
        void shouldFailToRemoveItemInCheckoutState() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.startCheckout();

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.removeItem(producto1.getProductId()));
        }

        @Test
        @DisplayName("Should fail to update quantity in EN_CHECKOUT state")
        void shouldFailToUpdateInCheckoutState() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.startCheckout();

            assertThrows(BusinessRuleViolation.class,
                    () -> carrito.updateQty(producto1.getProductId(), 5));
        }

        @Test
        @DisplayName("Should fail to startCheckout in ABANDONADO state")
        void shouldFailToStartCheckoutInAbandonedState() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);
            carrito.abandon();

            assertThrows(BusinessRuleViolation.class, () -> carrito.startCheckout());
        }

        @Test
        @DisplayName("Should fail to completeCheckout from ACTIVO state")
        void shouldFailToCompleteCheckoutFromActivo() {
            Carrito carrito = Carrito.create(customerId);
            carrito.addItem(producto1, 1);

            assertThrows(BusinessRuleViolation.class, () -> carrito.completeCheckout());
        }
    }
}
