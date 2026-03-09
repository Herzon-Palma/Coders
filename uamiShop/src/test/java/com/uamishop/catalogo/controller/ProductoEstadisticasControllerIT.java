package com.uamishop.catalogo.controller;

import com.uamishop.catalogo.controller.dto.ProductoEstadisticasResponse;
import com.uamishop.catalogo.domain.ProductoEstadisticas;
import com.uamishop.catalogo.repository.ProductoEstadisticasJpaRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para los endpoints de estadísticas de producto.
 * 
 * Estas pruebas verifican los endpoints REST directamente,
 * insertando datos de estadísticas en la BD para comprobar
 * que la capa de controlador los expone correctamente.
 * Las pruebas se realizan con JUnit 5 y Spring Boot Test.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Se carga el contexto de Spring y se
                                                                            // levanta un puerto aleatorio
class ProductoEstadisticasControllerIT {

    private static final String BASE_URL = "/api/v1/productos";
    // Se inyectan las dependencias necesarias para las pruebas para generar
    // peticiones HTTP y acceder a la BD
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductoEstadisticasJpaRepository estadisticasRepository;

    @AfterEach
    void tearDown() {
        estadisticasRepository.deleteAll();
    }

    // ==================== GET /api/v1/productos/{id}/estadisticas
    // ====================

    @Nested
    @DisplayName("GET /api/v1/productos/{id}/estadisticas")
    class ObtenerEstadisticasDeProducto {

        @Test
        @DisplayName("Retorna estadísticas en ceros para un producto sin ventas registradas")
        // Se crea un producto sin ventas registradas y se verifica que las estadísticas
        // sean cero
        void estadisticasProductoSinDatos() {
            UUID productoId = UUID.randomUUID();

            ResponseEntity<ProductoEstadisticasResponse> response = restTemplate.getForEntity(
                    BASE_URL + "/" + productoId + "/estadisticas",
                    ProductoEstadisticasResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            ProductoEstadisticasResponse body = response.getBody();
            assertEquals(0, body.ventasTotales(), "Un producto sin datos debe tener ventasTotales = 0");
            assertEquals(0, body.cantidadVendida(), "Un producto sin datos debe tener cantidadVendida = 0");
            assertEquals(0, body.vecesAgregadoAlCarrito(),
                    "Un producto sin datos debe tener vecesAgregadoAlCarrito = 0");
            assertNull(body.ultimaVentaAt(), "Un producto sin ventas debe tener ultimaVentaAt = null");
        }

        @Test
        @DisplayName("Retorna estadísticas correctas para un producto con ventas registradas")
        // Se crea un producto con ventas registradas y se verifica que las estadísticas
        // sean correctas
        void estadisticasProductoConDatos() {
            UUID productoId = UUID.randomUUID();

            // Insertar estadísticas directamente en la BD
            ProductoEstadisticas estadisticas = new ProductoEstadisticas(productoId, 5);
            estadisticas.setVecesAgregadoCarrito(12);
            estadisticasRepository.save(estadisticas);

            ResponseEntity<ProductoEstadisticasResponse> response = restTemplate.getForEntity(
                    BASE_URL + "/" + productoId + "/estadisticas",
                    ProductoEstadisticasResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            ProductoEstadisticasResponse body = response.getBody();
            assertEquals(5, body.ventasTotales(), "ventasTotales debe reflejar las ventas registradas");
            assertEquals(5, body.cantidadVendida(), "cantidadVendida debe reflejar las unidades vendidas");
            assertEquals(12, body.vecesAgregadoAlCarrito(),
                    "vecesAgregadoAlCarrito debe reflejar las veces agregado");
            assertNotNull(body.ultimaVentaAt(), "ultimaVentaAt no debe ser null si hubo ventas");
        }

        @Test
        @DisplayName("Retorna estadísticas actualizadas después de múltiples ventas")
        // Se simulan múltiples ventas para un producto y se verifica que las
        // estadísticas se actualicen correctamente
        void estadisticasProductoConMultiplesVentas() {
            UUID productoId = UUID.randomUUID();

            // Primera venta: 3 unidades
            ProductoEstadisticas estadisticas = new ProductoEstadisticas(productoId, 3);
            estadisticasRepository.save(estadisticas);

            // Simular segunda venta: 7 unidades más
            ProductoEstadisticas existente = estadisticasRepository.findById(productoId).orElseThrow();
            existente.registrarVenta(7);
            estadisticasRepository.save(existente);

            ResponseEntity<ProductoEstadisticasResponse> response = restTemplate.getForEntity(
                    BASE_URL + "/" + productoId + "/estadisticas",
                    ProductoEstadisticasResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            ProductoEstadisticasResponse body = response.getBody();
            assertNotNull(body);
            assertEquals(10, body.cantidadVendida(),
                    "cantidadVendida debe acumular ambas ventas: 3 + 7 = 10");
            assertEquals(10, body.ventasTotales(),
                    "ventasTotales debe acumular ambas ventas: 3 + 7 = 10");
        }
    }

    // ==================== GET /api/v1/productos/mas-vendidos ====================

    @Nested
    @DisplayName("GET /api/v1/productos/mas-vendidos")
    class ObtenerMasVendidos {

        @Test
        @DisplayName("Retorna lista vacía cuando no hay estadísticas registradas")
        // Se verifica que la lista de productos más vendidos esté vacía cuando no hay
        // estadísticas registradas
        void masVendidosSinDatos() {
            ResponseEntity<List<ProductoEstadisticasResponse>> response = restTemplate.exchange(
                    BASE_URL + "/mas-vendidos",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductoEstadisticasResponse>>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty(), "La lista debe estar vacía si no hay estadísticas");
        }

        @Test
        @DisplayName("Retorna productos ordenados por cantidad vendida de forma descendente")
        // Se verifica que la lista de productos más vendidos esté ordenada por cantidad
        // vendida de forma descendente
        void masVendidosOrdenadosDescendente() {
            // Producto A: 50 unidades vendidas
            UUID productoA = UUID.randomUUID();
            ProductoEstadisticas estA = new ProductoEstadisticas(productoA, 50);
            estadisticasRepository.save(estA);

            // Producto B: 100 unidades vendidas (el más vendido)
            UUID productoB = UUID.randomUUID();
            ProductoEstadisticas estB = new ProductoEstadisticas(productoB, 100);
            estadisticasRepository.save(estB);

            // Producto C: 25 unidades vendidas
            UUID productoC = UUID.randomUUID();
            ProductoEstadisticas estC = new ProductoEstadisticas(productoC, 25);
            estadisticasRepository.save(estC);

            ResponseEntity<List<ProductoEstadisticasResponse>> response = restTemplate.exchange(
                    BASE_URL + "/mas-vendidos",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductoEstadisticasResponse>>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            List<ProductoEstadisticasResponse> lista = response.getBody();
            assertEquals(3, lista.size(), "Deben retornarse los 3 productos");

            // Verificar orden descendente por cantidadVendida
            assertEquals(100, lista.get(0).cantidadVendida(), "El primer producto debe ser el más vendido (100)");
            assertEquals(50, lista.get(1).cantidadVendida(), "El segundo producto debe tener 50 ventas");
            assertEquals(25, lista.get(2).cantidadVendida(), "El tercer producto debe tener 25 ventas");
        }

        @Test
        @DisplayName("Respeta el parámetro limit para limitar resultados")
        // Se verifica que el parámetro limit funcione correctamente
        void masVendidosConLimit() {
            // Insertar 5 productos con diferentes cantidades vendidas
            for (int i = 1; i <= 5; i++) {
                UUID id = UUID.randomUUID();
                ProductoEstadisticas est = new ProductoEstadisticas(id, i * 10);
                estadisticasRepository.save(est);
            }

            // Pedir solo los top 2
            ResponseEntity<List<ProductoEstadisticasResponse>> response = restTemplate.exchange(
                    BASE_URL + "/mas-vendidos?limit=2",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductoEstadisticasResponse>>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size(),
                    "Solo deben retornarse 2 productos cuando limit=2");

            // El primero debe ser el de mayor cantidadVendida (50)
            assertEquals(50, response.getBody().get(0).cantidadVendida(),
                    "El primer resultado debe ser el producto con más ventas");
        }

        @Test
        @DisplayName("Usa limit=10 por defecto cuando no se especifica el parámetro")
        // Se verifica que el parámetro limit funcione correctamente cuando no se
        // especifica el parámetro
        void masVendidosLimitPorDefecto() {
            // Insertar 15 productos
            for (int i = 1; i <= 15; i++) {
                UUID id = UUID.randomUUID();
                ProductoEstadisticas est = new ProductoEstadisticas(id, i);
                estadisticasRepository.save(est);
            }

            ResponseEntity<List<ProductoEstadisticasResponse>> response = restTemplate.exchange(
                    BASE_URL + "/mas-vendidos",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductoEstadisticasResponse>>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(10, response.getBody().size(),
                    "Sin parámetro limit, deben retornarse máximo 10 productos (valor por defecto)");
        }
    }
}
