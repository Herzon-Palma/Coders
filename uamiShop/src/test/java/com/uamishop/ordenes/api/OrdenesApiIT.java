package com.uamishop.ordenes.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.uamishop.catalogo.api.CatalogoApi; // La interfaz
import com.uamishop.catalogo.api.ProductoResumen; // El DTO que devuelve la API
import com.uamishop.shared.domain.Money;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;

@SpringBootTest
public class OrdenesApiIT {

    @Autowired
    private OrdenesApi ordenesApi;

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private OrdenJpaRepository ordenRepository;

    // YA NO USAMOS REPOSITORIOS DE CATÁLOGO
    // Usamos el Mock de la API
    @MockBean
    private CatalogoApi catalogoApi;

    private UUID productoId;

    @BeforeEach
    void setUp() {
        productoId = UUID.randomUUID();

        // SIMULACIÓN: Programamos el Mock para que cuando OrdenesService pregunte, 
        // el catálogo responda con datos válidos sin ir a la base de datos.
        ProductoResumen mockProducto = new ProductoResumen(
                productoId,
                "Teclado Gamer",
                "TEC-001",
                Money.pesos(800).cantidad(),
                "MXN",
                UUID.randomUUID(),
                "Electrónicos",
                true // Disponible
        );

        // Configuramos las respuestas de la API mockeada
        when(catalogoApi.buscarProducto(productoId)).thenReturn(Optional.of(mockProducto));
        when(catalogoApi.estaDisponible(productoId)).thenReturn(true);
        when(catalogoApi.obtenerPrecio(productoId)).thenReturn(Optional.of(Money.pesos(800)));
    }

    @AfterEach
    void cleanUp() {
        ordenRepository.deleteAll();
        // Ya no limpiamos productos ni categorías porque no existen aquí
    }

    @Nested
    @DisplayName("Tests para el Gateway interno OrdenesApi")
    class ApiTest {

        @Test
        @DisplayName("Debe devolver el Resumen de la orden sin exponer las Entidades Root")
        void obtenerResumenValidoTest() {
            // Setup
            UUID clienteId = UUID.randomUUID();
            DireccionEnvio direccion = new DireccionEnvio(
                    "Ana Torres", "Avenida 456", "MTY", "NL", "54321", "México", "8112345678", "");
            
            OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, 2);

            // Guardamos usando el propio Service. 
            // Internamente el service llamará a nuestro Mock de catalogoApi.
            var ordenBD = ordenService.crearOrden(clienteId, direccion, List.of(item));

            // Execution
            Optional<OrdenResumen> response = ordenesApi.obtenerOrden(ordenBD.getId().id());

            // Assertions
            assertTrue(response.isPresent());
            OrdenResumen resumen = response.get();
            assertEquals(ordenBD.getId().id(), resumen.ordenId());
            assertEquals("PENDIENTE", resumen.estadoOrden());
            assertEquals("Teclado Gamer", resumen.items().get(0).nombreProducto());
        }

        @Test
        @DisplayName("Falla silenciosa al obtener ordenes inexistentes a través de Optional")
        void obtenerOrdenInexistenteTest() {
            Optional<OrdenResumen> response = ordenesApi.obtenerOrden(UUID.randomUUID());
            assertFalse(response.isPresent());
        }
    }
}