package com.uamishop.ordenes.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.uamishop.catalogo.domain.*;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.repository.ProductoRepository;
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

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Producto productoActivo;

    @BeforeEach
    void setUp() {
        Categoriaid categoriaId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(categoriaId, "Electrónicos", "Gadgets"));

        productoActivo = Producto.crear("Teclado Gamer", "Teclado mecánico RGB", "TEC-001", Money.pesos(800), categoriaId);
        productoActivo.agregarImagen(new Imagen("https://uami.mx/teclado.png", "Teclado", 1));
        productoActivo.activar();
        productoRepository.save(productoActivo);
    }

    @AfterEach
    void cleanUp() {
        ordenRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
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
            UUID productoId = productoActivo.getId().getValue();
            OrdenService.ItemDto item = new OrdenService.ItemDto(productoId, 2);

            // Guardamos usando el propio Service para tener una entidad base válida en DB
            var ordenBD = ordenService.crearOrden(clienteId, direccion, List.of(item));

            // Execution - Llamada a la API pública abstracta intermódulo
            Optional<OrdenResumen> response = ordenesApi.obtenerOrden(ordenBD.getId().id());

            // Assertions
            assertTrue(response.isPresent());
            OrdenResumen resumen = response.get();
            assertEquals(ordenBD.getId().id(), resumen.ordenId());
            assertEquals("PENDIENTE", resumen.estadoOrden());
            assertEquals(clienteId, resumen.clienteId().getId());
            assertEquals(1, resumen.items().size());
            assertEquals(2, resumen.items().get(0).cantidad());
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
