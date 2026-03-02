package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.ProductoId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.shared.domain.ClienteId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    private Carrito carrito;
    private ClienteId clienteId;
    private ProductoRef productoBase;
    private Money precioBase;

    @BeforeEach
    void setUp() {
        clienteId = ClienteId.generar();
        carrito = Carrito.crear(clienteId);
        precioBase = new Money(new BigDecimal("100.00"), "MXN");
        productoBase = new ProductoRef(ProductoId.generar(), "Laptop Gaming", "SKU-001");
    }

    @Test
    @DisplayName("RN-VEN-01: No debe permitir agregar cantidad cero o negativa")
    void testAgregarCantidadInvalida() {
        assertThrows(DomainException.class, () -> carrito.agregarProducto(productoBase, 0, precioBase));
    }

    @Test
    @DisplayName("RN-VEN-02: No debe permitir más de 10 unidades de un mismo producto")
    void testCantidadMaximaPorProducto() {
        assertThrows(DomainException.class, () -> carrito.agregarProducto(productoBase, 11, precioBase));
    }

    @Test
    @DisplayName("RN-VEN-04: Si el producto ya existe, debe sumar la cantidad")
    void testSumarCantidadProductoExistente() {
        carrito.agregarProducto(productoBase, 2, precioBase);
        carrito.agregarProducto(productoBase, 3, precioBase);

        assertEquals(5, carrito.obtenerCantidadItems());
    }

    @Test
    @DisplayName("RN-VEN-03: Un carrito no debe tener más de 20 productos diferentes")
    void testMaximoProductosDiferentes() {
        for (int i = 0; i < 20; i++) {
            ProductoRef p = new ProductoRef(ProductoId.generar(), "Prod " + i, "PRO-0" + (i < 10 ? "0" + i : i));
            carrito.agregarProducto(p, 1, precioBase);
        }

        ProductoRef producto21 = new ProductoRef(ProductoId.generar(), "Prod 21", "PRO-021");
        assertThrows(DomainException.class, () -> carrito.agregarProducto(producto21, 1, precioBase));
    }

    @Test
    @DisplayName("RN-VEN-10 y 12: Iniciar checkout requiere productos y monto mínimo ($50)")
    void testMontoMinimoCheckout() {
        // Probamos con un producto de $10 (menor a $50)
        ProductoRef barato = new ProductoRef(ProductoId.generar(), "Chicle", "CHI-001");
        Money precioBarato = new Money(new BigDecimal("10.00"), "MXN");

        carrito.agregarProducto(barato, 1, precioBarato);

        assertThrows(DomainException.class, () -> carrito.iniciarCheckout());
    }

    @Test
    @DisplayName("RN-VEN-11: El estado debe cambiar a EN_CHECKOUT al iniciar")
    void testEstadoCheckout() {
        carrito.agregarProducto(productoBase, 1, precioBase); // $100 > $50
        carrito.iniciarCheckout();

        assertEquals(EstadoCarrito.CHECKOUT, carrito.getEstado());
    }

    @Test
    @DisplayName("RN-VEN-06: No se puede modificar cantidad si está en checkout")
    void testModificarEnCheckout() {
        carrito.agregarProducto(productoBase, 1, precioBase);
        carrito.iniciarCheckout();

        assertThrows(DomainException.class, () -> carrito.modificarCantidad(productoBase.productoId(), 5));
    }

    @Test
    @DisplayName("RN-VEN-16: El descuento no puede ser mayor al 30% del subtotal")
    void testDescuentoMaximo() {
        carrito.agregarProducto(productoBase, 1, precioBase); // Subtotal $100

        // Intentamos aplicar un descuento de $40 (40% de $100)
        DescuentoAplicado descuentoExcesivo = new DescuentoAplicado(
                "CUPON40",
                TipoDescuento.MONTO_FIJO,
                new BigDecimal("40.00"),
                new Money(new BigDecimal("40.00"), "MXN"));

        assertThrows(DomainException.class, () -> carrito.aplicarDescuento(descuentoExcesivo));
    }
}
