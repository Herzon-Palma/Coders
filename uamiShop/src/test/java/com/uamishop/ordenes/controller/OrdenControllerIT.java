package com.uamishop.ordenes.controller;

import com.uamishop.catalogo.domain.*;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.repository.ProductoRepository;
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
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para OrdenController.
 * Ejecuta contra el contexto completo con BD H2 en memoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrdenControllerIT {

        private static final String BASE_URL = "/api/v1/ordenes";

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private OrdenJpaRepository ordenRepository;

        @Autowired
        private ProductoRepository productoRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private CarritoRepository carritoRepository;

        private Producto productoActivo;

        @BeforeEach
        void setUp() {
                Categoriaid categoriaId = Categoriaid.generar();
                categoriaRepository.save(new Categoria(categoriaId, "Electrónicos", "Gadgets"));

                productoActivo = Producto.crear("Producto Test", "Descripción del producto", "PRD-001",
                                Money.pesos(100), categoriaId);
                productoActivo.agregarImagen(new Imagen("https://uami.mx/producto.png", "Producto", 1));
                productoActivo.activar();
                productoRepository.save(productoActivo);
        }

        @AfterEach
        void cleanUp() {
                ordenRepository.deleteAll();
                carritoRepository.deleteAll();
                productoRepository.deleteAll();
                categoriaRepository.deleteAll();
        }

        @Nested
        @DisplayName("POST /api/v1/ordenes")
        class CrearOrden {

                @Test
                @DisplayName("Crea orden exitosamente y retorna 200 OK")
                void crearOrdenTest() {
                        // El código postal debe ser 5 dígitos y el teléfono 10 dígitos (reglas en
                        // DireccionEnvio)
                        DireccionEnvio direccion = new DireccionEnvio(
                                        "Juan Perez", "Calle 123", "CDMX", "CDMX", "12345", "México", "5512345678",
                                        "Dejar en recepción");

                        UUID productoId = productoActivo.getId().getValue();

                        OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, 2);

                        CrearOrdenRequest request = new CrearOrdenRequest(
                                        UUID.randomUUID(),
                                        direccion,
                                        List.of(item));

                        // Invocamos el endpoint POST /api/v1/ordenes
                        ResponseEntity<String> response = restTemplate.postForEntity(
                                        BASE_URL,
                                        request,
                                        String.class);

                        // Validamos que la orden haya sido creada exitosamente devolviendo un OK (200)
                        assertEquals(HttpStatus.OK, response.getStatusCode(),
                                        "Error en creación: " + response.getBody());
                        assertNotNull(response.getBody(), "El cuerpo de la respuesta está vacío");

                        // Comprobamos que el servidor la inicializó como PENDIENTE
                        assertTrue(response.getBody().contains("\"PENDIENTE\"")
                                        || response.getBody().contains("PENDIENTE"));
                }
        }

        @Nested
        @DisplayName("GET /api/v1/ordenes/{id}")
        class ObtenerOrden {

                @Test
                @DisplayName("Retorna error 400 u otro código al buscar id inexistente")
                void obtenerOrdenNoEncontrada() {
                        UUID idInexistente = UUID.randomUUID();

                        ResponseEntity<String> response = restTemplate.getForEntity(
                                        BASE_URL + "/" + idInexistente,
                                        String.class);

                        // La base de datos no tiene la orden, por lo que arroja excepcion de Negocio o
                        // Controlador (404 Not Found)
                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                                        "Error al buscar orden inexistente: " + response.getBody());
                        assertTrue(response.getBody().contains("Orden no encontrada"));
                }
        }

        @Nested
        @DisplayName("POST /api/v1/ordenes/{id}/confirmar")
        class ConfirmarOrden {

                @Test
                @DisplayName("Confirma una orden existente")
                void confirmarOrdenTest() {
                        // Para confirmar una orden, primero necesitamos crear una
                        DireccionEnvio direccion = new DireccionEnvio(
                                        "Ana Torres", "Avenida 456", "MTY", "NL", "54321", "México", "8112345678", "");
                        UUID productoId = productoActivo.getId().getValue();
                        OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, 1);

                        CrearOrdenRequest createRequest = new CrearOrdenRequest(UUID.randomUUID(), direccion,
                                        List.of(item));

                        // Creamos la orden vía API mapeando directamente para extraer su id
                        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                                        BASE_URL,
                                        createRequest,
                                        String.class);

                        assertEquals(HttpStatus.OK, createResponse.getStatusCode(),
                                        "Falló crear orden pre-confirmacion: " + createResponse.getBody());
                        // Extraer UUID con regex o desde JSON (Como estamos usando String, busquemos el
                        // "id":"uuid")
                        String json = createResponse.getBody();
                        int start = json.indexOf("\"id\":\"") + 6;
                        UUID ordenId = UUID.fromString(json.substring(start, start + 36));

                        // Ejecutamos la confirmación (POST)
                        ResponseEntity<String> confirmResponse = restTemplate.postForEntity(
                                        BASE_URL + "/" + ordenId + "/confirmar",
                                        null,
                                        String.class);

                        assertEquals(HttpStatus.OK, confirmResponse.getStatusCode(),
                                        "Error en confirmación: " + confirmResponse.getBody());
                        assertTrue(confirmResponse.getBody().contains("CONFIRMADA"),
                                        "El JSON no contiene CONFIRMADA. Body devuelto: " + confirmResponse.getBody());
                }
        }

        @Nested
        @DisplayName("POST /api/v1/ordenes/desde-carrito")
        class CrearOrdenDesdeCarrito {

                @Test
                @DisplayName("Crea orden desde carrito y completa el checkout asíncronamente")
                void crearOrdenDesdeCarritoTest() {
                        // 1. Preparar escenario
                        UUID clienteId = UUID.randomUUID();
                        DireccionEnvio direccion = new DireccionEnvio(
                                        "Carlos Ruiz", "Av. Reforma 50", "CDMX", "CDMX", "11000", "México",
                                        "5598765432",
                                        "Piso 10");

                        // Crear y guardar un carrito en estado CHECKOUT para el cliente
                        Carrito carrito = Carrito.crear(new ClienteId(clienteId));
                        ProductoRef ref = new ProductoRef(new Productoid(productoActivo.getId().getValue()),
                                        productoActivo.getNombre(), productoActivo.getSku());
                        carrito.agregarProducto(ref, 1, productoActivo.getPrecio());
                        carrito.iniciarCheckout();
                        carrito = carritoRepository.save(carrito);
                        UUID carritoId = carrito.getId().id();

                        // 2. Ejecutar petición
                        CrearDesdeCarritoRequest request = new CrearDesdeCarritoRequest(clienteId, direccion);
                        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(
                                        BASE_URL + "/desde-carrito",
                                        request,
                                        OrdenResponse.class);

                        // 3. Validaciones inmediatas (Sincrónicas)
                        assertEquals(HttpStatus.OK, response.getStatusCode());
                        assertNotNull(response.getBody());
                        assertEquals("PENDIENTE", response.getBody().estadoOrden());

                        // 4. Validación asíncrona (Eventualmente el carrito debe estar COMPLETADO)
                        await().atMost(5, TimeUnit.SECONDS).until(() -> {
                                Carrito c = carritoRepository
                                                .findById(new com.uamishop.ventas.domain.CarritoId(carritoId))
                                                .orElse(null);
                                return c != null && c.getEstado() == EstadoCarrito.COMPLETADO;
                        });

                        // 5. Verificar que el carrito realmente cambió a COMPLETADO
                        Carrito carritoFinal = carritoRepository
                                        .findById(new com.uamishop.ventas.domain.CarritoId(carritoId))
                                        .orElseThrow();
                        assertEquals(EstadoCarrito.COMPLETADO, carritoFinal.getEstado());
                }
        }
}
