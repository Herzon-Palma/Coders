package com.uamishop.ventas.api;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.EstadoCarrito;
import com.uamishop.ventas.repository.CarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VentasApiIT {

    @Autowired
    private VentasApi ventasApi;

    @Autowired
    private CarritoRepository carritoRepository;

    private CarritoId carritoId;
    private ClienteId clienteId;

    @BeforeEach
    void setUp() {
        carritoRepository.deleteAll();
        clienteId = ClienteId.generar();
        Carrito carrito = Carrito.crear(clienteId);

        // Agregar un producto para que pueda pasar a checkout
        ProductoRef producto = new ProductoRef(Productoid.generar(), "Producto Test", "TES-001");
        carrito.agregarProducto(producto, 1, Money.pesos(100));

        carritoRepository.save(carrito);
        carritoId = carrito.getId();
    }

    @Test
    @DisplayName("obtenerCarrito: devuelve resumen correctamente")
    void obtenerCarrito_exitoso() {
        CarritoResumen resumen = ventasApi.obtenerCarrito(carritoId.id());

        assertNotNull(resumen);
        assertEquals(carritoId.id(), resumen.carritoId());
        assertEquals(clienteId, resumen.clienteId());
        assertEquals("ACTIVO", resumen.estado());
        assertEquals(1, resumen.items().size());
        assertEquals("Producto Test", resumen.items().get(0).nombreProducto());
    }

    @Test
    @DisplayName("completarCheckout: cambia el estado del carrito a COMPLETADO")
    void completarCheckout_exitoso() {
        // Primero debe estar en CHECKOUT
        Carrito carrito = carritoRepository.findById(carritoId).orElseThrow();
        carrito.iniciarCheckout();
        carritoRepository.save(carrito);

        ventasApi.completarCheckout(carritoId.id());

        Carrito actualizado = carritoRepository.findById(carritoId).orElseThrow();
        assertEquals(EstadoCarrito.COMPLETADO, actualizado.getEstado());
    }
}
