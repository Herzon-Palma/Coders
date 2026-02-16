package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.exception.CarritoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    //Datos reutilizables
    private final ClienteId clienteId = new ClienteId(UUID.randomUUID());
    private final ProductoRef iphoneRef = new ProductoRef(UUID.randomUUID(), "iPhone 15");
    private final Money precio = Money.of(20000.00);

    @Test
    @DisplayName("Debe agregar un producto nuevo correctamente")
    void agregarProductoNuevo() {
        Carrito carrito = new Carrito(clienteId);

        carrito.agregarProducto(iphoneRef, 1, precio);

        assertEquals(1, carrito.getItems().size());
        assertEquals("iPhone 15", carrito.getItems().get(0).getProducto().nombre());
        assertEquals(Money.of(20000.00), carrito.getItems().get(0).getPrecioUnitario());
    }

    @Test
    @DisplayName("Debe sumar la cantidad si el producto ya existe")
    void agregarProductoExistente() {
        Carrito carrito = new Carrito(clienteId);

        //Agregamos 1
        carrito.agregarProducto(iphoneRef, 1, precio);
        //Agregamos 2 del m,ismo
        carrito.agregarProducto(iphoneRef, 2, precio);

        assertEquals(1, carrito.getItems().size());
        assertEquals(3, carrito.getItems().get(0).getCantidad());//Cantidad total 3
    }

    @Test
    @DisplayName("No debe permitir modificar el carrito si ya está en checkout")
    void errorModificarEnCheckout() {
        Carrito carrito = new Carrito(clienteId);
        carrito.agregarProducto(iphoneRef, 1, precio);
        
        //Finalizamos la compra
        carrito.iniciarCheckout();

        //Intentamos agregar otro producto
        ProductoRef fundaRef = new ProductoRef(UUID.randomUUID(), "Funda");
        
        assertThrows(CarritoException.class, () -> {
            carrito.agregarProducto(fundaRef, 1, Money.of(500));
        });
    }
}
