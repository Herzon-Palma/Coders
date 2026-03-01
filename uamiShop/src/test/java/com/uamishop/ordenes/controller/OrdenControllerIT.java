package com.uamishop.ordenes.controller;

import com.uamishop.ordenes.domain.EstadoOrden;
import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;

import org.junit.jupiter.api.AfterEach;
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

    private static final String BASE_URL = "/api/ordenes";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrdenJpaRepository ordenRepository;

    @AfterEach
    void cleanUp() {
        // Al final de cada prueba, limpiamos la base de datos de órdenes
        ordenRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/ordenes")
    class CrearOrden {

        @Test
        @DisplayName("Crea orden exitosamente y retorna 200 OK")
        void crearOrdenTest() {
            // El código postal debe ser 5 dígitos y el teléfono 10 dígitos (reglas en
            // DireccionEnvio)
            DireccionEnvio direccion = new DireccionEnvio(
                    "Juan Perez", "Calle 123", "CDMX", "CDMX", "12345", "México", "5512345678", "Dejar en recepción");

            // Generar un UUID que devuelva letras y números para cumplir el SKU (generado
            // en OrdenService)
            UUID productoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, "Producto Test", 2, 100.0);

            CrearOrdenRequest request = new CrearOrdenRequest(
                    UUID.randomUUID(),
                    direccion,
                    List.of(item));

            // Invocamos el endpoint POST /api/ordenes
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL,
                    request,
                    String.class);

            // Validamos que la orden haya sido creada exitosamente devolviendo un OK (200)
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Error en creación: " + response.getBody());
            assertNotNull(response.getBody(), "El cuerpo de la respuesta está vacío");

            // Comprobamos que el servidor la inicializó como PENDIENTE
            assertTrue(response.getBody().contains("\"PENDIENTE\"") || response.getBody().contains("PENDIENTE"));
        }
    }

    @Nested
    @DisplayName("GET /api/ordenes/{id}")
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
    @DisplayName("POST /api/ordenes/{id}/confirmar")
    class ConfirmarOrden {

        @Test
        @DisplayName("Confirma una orden existente")
        void confirmarOrdenTest() {
            // Para confirmar una orden, primero necesitamos crear una
            DireccionEnvio direccion = new DireccionEnvio(
                    "Ana Torres", "Avenida 456", "MTY", "NL", "54321", "México", "8112345678", "");
            UUID productoId = UUID.fromString("987e4567-e89b-12d3-a456-426614174000");
            OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, "Teclado Mecanico", 1, 1500.0);

            CrearOrdenRequest createRequest = new CrearOrdenRequest(UUID.randomUUID(), direccion, List.of(item));

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
}
