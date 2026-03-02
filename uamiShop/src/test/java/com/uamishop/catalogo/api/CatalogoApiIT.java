package com.uamishop.catalogo.api;

import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import com.uamishop.catalogo.domain.Imagen;
import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CatalogoApiIT {

    @Autowired
    private CatalogoApi catalogoApi; 
    /*  Inyecta ProductoService (implementa CatalogoApi),
        lo que nos permite probar la lógica de negocio real con una base de datos en memoria (H2) durante las pruebas de integración. 
        No es un mock, es el servicio real con su repositorio real, pero aislado del resto de la aplicación.
    */
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoriaid categoriaId;
    private Producto productoActivo;
    private Producto productoInactivo;

    // Antes de cada prueba, limpiamos la base de datos y preparamos algunos datos de prueba
    // los ponemos antes para no repetir código en cada test
    // Esto también garantiza que cada prueba se ejecute en un estado limpio e independiente, evitando interferencias entre pruebas.
    // Para eso vamos a usar Beforeeach
    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        // Crear categoría de prueba
        categoriaId = Categoriaid.generar();
        categoriaRepository.save(new Categoria(categoriaId, "Electrónicos", "Gadgets y dispositivos"));

        // Producto activo (con imagen para poder activarlo) y se pueda probar la disponibilidad
        productoActivo = Producto.crear("Laptop Gaming", "Core i9 32GB", "LAP-001", Money.pesos(25000), categoriaId);
        productoActivo.agregarImagen(new Imagen("https://uami.mx/laptop.png", "Vista frontal", 1));
        productoActivo.activar();
        productoRepository.save(productoActivo);

        // Producto inactivo (sin activar), justo cómo el anterior pero sin llamar a activar(), para probar casos de productos no disponibles.
        productoInactivo = Producto.crear("Monitor 4K", "32 pulgadas IPS", "MON-001", Money.pesos(8000), categoriaId);
        productoRepository.save(productoInactivo);
    }

    @Test
    @DisplayName("buscarProducto: devuelve resumen cuando el producto existe")
    void buscarProducto_existente() {
        UUID id = productoActivo.getId().valor();

        Optional<ProductoResumen> resultado = catalogoApi.buscarProducto(id);

        assertTrue(resultado.isPresent());
        ProductoResumen resumen = resultado.get();
        assertEquals("Laptop Gaming", resumen.nombre());
        assertEquals("LAP-001", resumen.sku());
        assertEquals(0, BigDecimal.valueOf(25000).compareTo(resumen.precio()));
        assertEquals("MXN", resumen.moneda());
        assertEquals(categoriaId.valor(), resumen.categoriaId());
        assertEquals("Electrónicos", resumen.categoriaNombre());
        assertTrue(resumen.disponible());
    }

    @Test
    @DisplayName("buscarProducto: devuelve vacío cuando el producto no existe")
    void buscarProducto_inexistente() {
        Optional<ProductoResumen> resultado = catalogoApi.buscarProducto(UUID.randomUUID());

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("buscarProductos: devuelve múltiples productos por sus IDs")
    void buscarProductos_lote() {
        List<UUID> ids = List.of(
                productoActivo.getId().valor(),
                productoInactivo.getId().valor()
        );

        List<ProductoResumen> resultados = catalogoApi.buscarProductos(ids);

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("buscarProductos: ignora IDs inexistentes sin lanzar error")
    void buscarProductos_conIdsInexistentes() {
        List<UUID> ids = List.of(
                productoActivo.getId().valor(),
                UUID.randomUUID() // no existe
        );

        List<ProductoResumen> resultados = catalogoApi.buscarProductos(ids);

        assertEquals(1, resultados.size());
        assertEquals("Laptop Gaming", resultados.get(0).nombre());
    }

    @Test
    @DisplayName("buscarProductos: lista vacía devuelve lista vacía")
    void buscarProductos_listaVacia() {
        List<ProductoResumen> resultados = catalogoApi.buscarProductos(List.of());

        assertTrue(resultados.isEmpty());
    }


    @Test
    @DisplayName("existeProducto: true si el producto existe")
    void existeProducto_true() {
        assertTrue(catalogoApi.existeProducto(productoActivo.getId().valor()));
    }

    @Test
    @DisplayName("existeProducto: false si el producto no existe")
    void existeProducto_false() {
        assertFalse(catalogoApi.existeProducto(UUID.randomUUID()));
    }

    @Test
    @DisplayName("estaDisponible: true para producto activo")
    void estaDisponible_activo() {
        assertTrue(catalogoApi.estaDisponible(productoActivo.getId().valor()));
    }

    @Test
    @DisplayName("estaDisponible: false para producto inactivo")
    void estaDisponible_inactivo() {
        assertFalse(catalogoApi.estaDisponible(productoInactivo.getId().valor()));
    }

    @Test
    @DisplayName("estaDisponible: false para producto inexistente")
    void estaDisponible_inexistente() {
        assertFalse(catalogoApi.estaDisponible(UUID.randomUUID()));
    }

    @Test
    @DisplayName("obtenerPrecio: devuelve el precio del producto")
    void obtenerPrecio_existente() {
        Optional<Money> precio = catalogoApi.obtenerPrecio(productoActivo.getId().valor());

        assertTrue(precio.isPresent());
        assertEquals(0, BigDecimal.valueOf(25000).compareTo(precio.get().cantidad()));
        assertEquals("MXN", precio.get().moneda());
    }

    @Test
    @DisplayName("obtenerPrecio: vacío para producto inexistente")
    void obtenerPrecio_inexistente() {
        assertTrue(catalogoApi.obtenerPrecio(UUID.randomUUID()).isEmpty());
    }

    @Test
    @DisplayName("existeCategoria: true si la categoría existe")
    void existeCategoria_true() {
        assertTrue(catalogoApi.existeCategoria(categoriaId.valor()));
    }

    @Test
    @DisplayName("existeCategoria: false si la categoría no existe")
    void existeCategoria_false() {
        assertFalse(catalogoApi.existeCategoria(UUID.randomUUID()));
    }
}
