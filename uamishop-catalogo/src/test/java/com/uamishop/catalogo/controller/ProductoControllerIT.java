package com.uamishop.catalogo.controller;

import com.uamishop.catalogo.controller.dto.ProductoRequest;
import com.uamishop.catalogo.controller.dto.ProductoResponse;
import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración del ProductoController del microservicio catálogo.
 * Migrado del monolito — usa H2 en memoria y el perfil 'catalogo-local'
 * para activar los controllers que solo existen en el microservicio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("catalogo-local")
public class ProductoControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @AfterEach
    void tearDown() {
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/productos - Crear producto exitosamente")
    void crearProductoTest() {
        Categoriaid categoriaId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(categoriaId, "Electrónicos", "Gadgets"));

        ProductoRequest request = new ProductoRequest(
                "Laptop Gaming",
                "Procesador i7",
                "LAP-001",
                BigDecimal.valueOf(1500.00),
                "MXN",
                categoriaId.getValue(),
                null);

        ResponseEntity<ProductoResponse> response = restTemplate.postForEntity(
                "/api/v1/productos",
                request,
                ProductoResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Laptop Gaming", response.getBody().nombre());
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - 404 producto no encontrado")
    void obtenerProductoInexistente() {
        UUID idInexistente = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/productos/" + idInexistente,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("PUT /api/v1/productos/{id} - Actualizar producto")
    void actualizarProductoTest() {
        Categoriaid catId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(catId, "Hardware", "PC Parts"));

        Productoid pId = Productoid.generar();
        productoRepository.save(new Producto(pId, "Teclado", "Mecánico", "TEC-001", Money.pesos(50), catId));

        ProductoRequest updateRequest = new ProductoRequest(
                "Teclado RGB", "Mecánico Gamer", "TEC-001", BigDecimal.valueOf(70), "MXN", catId.getValue(), null);

        HttpEntity<ProductoRequest> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<ProductoResponse> response = restTemplate.exchange(
                "/api/v1/productos/" + pId.getValue(),
                HttpMethod.PUT,
                requestEntity,
                ProductoResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Teclado RGB", response.getBody().nombre());
    }
}
