package com.uamishop.ordenes.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// IMPORTANTE: Ya no importamos dominios de catálogo, solo la API y DTOs
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.EstadoCarrito;
import com.uamishop.ventas.repository.CarritoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrdenControllerIT {

    private static final String BASE_URL = "/api/v1/ordenes";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrdenJpaRepository ordenRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    // SUSTITUIMOS LOS REPOSITORIOS POR EL MOCK DE LA API
    @MockBean
    private CatalogoApi catalogoApi;

    private UUID productoId;

    @BeforeEach
    void setUp() {
        productoId = UUID.randomUUID();

        // SIMULACIÓN: Programamos la respuesta de la API de Catálogo
        // para que cuando el controlador llame al servicio, el servicio reciba esto:
        ProductoResumen resumen = new ProductoResumen(
                productoId,
                "Producto Test",
                "PRD-001",
                Money.pesos(100).cantidad(),
                "MXN",
                UUID.randomUUID(),
                "Electrónicos",
                true
        );

        when(catalogoApi.buscarProducto(productoId)).thenReturn(Optional.of(resumen));
        when(catalogoApi.estaDisponible(productoId)).thenReturn(true);
        when(catalogoApi.obtenerPrecio(productoId)).thenReturn(Optional.of(Money.pesos(100)));
    }

    @AfterEach
    void cleanUp() {
        ordenRepository.deleteAll();
        carritoRepository.deleteAll();
        // Ya no limpiamos productos ni categorías porque no hay repositorios aquí
    }

    @Nested
    @DisplayName("POST /api/v1/ordenes")
    class CrearOrden {

        @Test
        @DisplayName("Crea orden exitosamente y retorna 200 OK")
        void crearOrdenTest() {
            DireccionEnvio direccion = new DireccionEnvio(
                    "Juan Perez", "Calle 123", "CDMX", "CDMX", "12345", "México", "5512345678",
                    "Dejar en recepción");

            OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, 2);

            CrearOrdenRequest request = new CrearOrdenRequest(
                    UUID.randomUUID(),
                    direccion,
                    List.of(item));

            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            // Verificamos que el nombre del producto (que viene del Mock) aparezca en la respuesta
            assertTrue(response.getBody().contains("Producto Test"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/ordenes/desde-carrito")
    class CrearOrdenDesdeCarrito {

        @Test
        @DisplayName("Crea orden desde carrito y completa el checkout asíncronamente")
        void crearOrdenDesdeCarritoTest() {
            UUID clienteId = UUID.randomUUID();
            DireccionEnvio direccion = new DireccionEnvio(
                    "Carlos Ruiz", "Av. Reforma 50", "CDMX", "CDMX", "11000", "México",
                    "5598765432", "Piso 10");

            // Setup del carrito (Ventas sigue siendo parte del monolito)
            Carrito carrito = Carrito.crear(new ClienteId(clienteId));
            
            // Usamos el productoId que definimos en el Mock
            ProductoRef ref = new ProductoRef(new Productoid(productoId), "Producto Test", "PRD-001");
            carrito.agregarProducto(ref, 1, Money.pesos(100));
            carrito.iniciarCheckout();
            carrito = carritoRepository.save(carrito);
            
            UUID carritoId = carrito.getId().id();

            CrearDesdeCarritoRequest request = new CrearDesdeCarritoRequest(clienteId, direccion);
            ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/desde-carrito", request, OrdenResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            // Verificación asíncrona del carrito (Lógica de Ventas/Órdenes)
            await().atMost(5, TimeUnit.SECONDS).until(() -> {
                Carrito c = carritoRepository
                        .findById(new com.uamishop.ventas.domain.CarritoId(carritoId))
                        .orElse(null);
                return c != null && c.getEstado() == EstadoCarrito.COMPLETADO;
            });
        }
    }
}