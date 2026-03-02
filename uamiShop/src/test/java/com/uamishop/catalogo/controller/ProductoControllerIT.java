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

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductoControllerIT {

    @Autowired
    private TestRestTemplate restTemplate; // El cliente HTTP real

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
    @DisplayName("POST /api/v1/productos - Crear producto exitosamente (RestTemplate)")
    void crearProductoTest() {

        Categoriaid categoriaId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(categoriaId, "Electrónicos", "Gadgets"));

        ProductoRequest request = new ProductoRequest(
                "Laptop Gaming",
                "Procesador i7",
                "LAP-001",
                BigDecimal.valueOf(1500.00),
                "MXN",
                categoriaId.getValue());

        ResponseEntity<ProductoResponse> response = restTemplate.postForEntity(
                "/api/v1/productos",
                request,
                ProductoResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Laptop Gaming", response.getBody().nombre());
    }

    // Haré un test para el caso de obtener un producto que no existe, esperando un
    // 400 Not Found y un mensaje de error adecuado
    @Test
    @DisplayName(" 404 producto no encontrado")
    void obtenerProductoInexistente() {
        UUID idInexistente = UUID.randomUUID();

        // Usamos exchange para obtener el cuerpo como un String o un Map y validar el
        // error
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/productos/" + idInexistente,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("Actualizar producto")
    void actualizarProductoTest() {

        Categoriaid catId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(catId, "Hardware", "PC Parts"));

        Productoid pId = Productoid.generar();
        productoRepository.save(new Producto(pId, "Teclado", "Mecánico", "TEC-001", Money.pesos(50), catId));

        ProductoRequest updateRequest = new ProductoRequest(
                "Teclado RGB", "Mecánico Gamer", "TEC-001", BigDecimal.valueOf(70), "MXN", catId.getValue());

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