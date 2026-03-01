package com.uamishop.ventas.controller;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.repository.CarritoRepository;
import com.uamishop.ventas.controller.dto.CarritoRequest;
import com.uamishop.ventas.controller.dto.CarritoResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarritoControllerIT {

    private static final String BASE_URL = "/api/v1/carritos";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarritoRepository carritoRepository;

    @AfterEach
    void cleanUp() {
        carritoRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/carritos")
    class CrearCarrito {

        @Test
        @DisplayName("Crea un carrito exitosamente")
        void crearCarritoTest() {
            ClienteId clienteId = ClienteId.generar();

            ResponseEntity<CarritoResponse> response = restTemplate.postForEntity(
                    BASE_URL,
                    clienteId, // Mapeado directamente a Body
                    CarritoResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Fallo en creación del carrito");
            assertNotNull(response.getBody(), "El cuerpo está vacío");
            assertNotNull(response.getBody().id(), "No se asignó ID de carrito");
            assertEquals("ACTIVO", response.getBody().estado());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/carritos/{id}")
    class ObtenerCarrito {

        @Test
        @DisplayName("Retorna error 404 al buscar carrito inextistente")
        void obtenerCarritoNoEncontrado() {
            UUID randomId = UUID.randomUUID();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URL + "/" + randomId,
                    String.class);

            // CarritoNotFoundException debería disparar 404 de acuerdo con
            // GlobalExceptionHandler o Default
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Debe retornar 404 Not Found");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/carritos/{id}/items")
    class AgregarProducto {

        @Test
        @DisplayName("Agrega producto a un carrito activo exitosamente")
        void agregarProductoCarritoTest() {
            // Setup - Creamos el carrito usando el API para obtener su ID real
            ClienteId clienteId = ClienteId.generar();
            ResponseEntity<CarritoResponse> createResponse = restTemplate.postForEntity(
                    BASE_URL, clienteId, CarritoResponse.class);
            assertEquals(HttpStatus.OK, createResponse.getStatusCode());

            UUID carritoId = createResponse.getBody().id();

            // Preparar payload para agregar un item
            CarritoRequest itemRequest = new CarritoRequest(UUID.randomUUID(), 2);

            ResponseEntity<CarritoResponse> addResponse = restTemplate.postForEntity(
                    BASE_URL + "/" + carritoId + "/items",
                    itemRequest,
                    CarritoResponse.class);

            assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "Error HTTP al agregar item");
            CarritoResponse body = addResponse.getBody();
            assertNotNull(body);
            assertEquals(1, body.items().size(), "El carrito debería tener 1 producto");
            assertEquals(2, body.items().get(0).cantidad().intValue(), "El producto debería tener 2 unidades");
        }
    }
}
