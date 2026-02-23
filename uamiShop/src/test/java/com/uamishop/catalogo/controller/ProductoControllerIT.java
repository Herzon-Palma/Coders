package com.uamishop.catalogo.controller;

import java.math.BigDecimal;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.catalogo.controller.dto.ProductoRequest;
import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;

@SpringBootTest
@AutoConfigureMockMvc // Configura MockMvc para pruebas de integración
@Transactional // Asegura que cada prueba se ejecute en una transacción que se revertirá al finalizar
public class ProductoControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Autowired
    private ProductoRepository productoRepository; // Para interactuar con la base de datos durante las pruebas
    
    @Autowired
    private CategoriaRepository categoriaRepository; // Para interactuar con la base de datos durante las pruebas
    
    @Test
    @DisplayName("POST /api/productos - Crear producto exitosamente")
    void crearProductoTest() throws Exception {
        Categoriaid categoriaId = Categoriaid.generar();
        Categoria categoria = new Categoria(categoriaId, "Electrónicos", "Dispositivos electrónicos");
        categoriaRepository.save(categoria);
        // necesitamos crar un request con datos válidos para crear un producto
        //ProductoRequest(@NotEmpty(message = "El nombre no puede estar vacío") String nombre, @NotEmpty(message = "La descripción no puede estar vacía") String descripcion,@Positive(message = "El precio debe ser positivo") BigDecimal precio,@NotEmpty String moneda,@NotNull UUID categoriaid) 
        ProductoRequest request = new ProductoRequest(
            "Laptop Gaming",
            "Laptop con procesador Intel i7, 16GB RAM, 512GB SSD",
            BigDecimal.valueOf(1500.00),
            "MXN",
            categoriaId.getValue() // Suponiendo que esta categoría existe en la base de datos
        );

        // Ejecutamos y verificamos
        mockMvc.perform(post("/api/productos")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated()) // Verifica que el servicio devuelva 201
            .andExpect(jsonPath("$.nombre").value("Laptop Gaming"));
    }

    @Test
    @DisplayName("Debe retornarn un  404 Not Found al obtener un producto inexistente")
    void obtenerProductoInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/productos/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Producto no encontrado con ID: " + idInexistente));
    }

    @Test
    @DisplayName("Debe obtener la lista de los productos")
    void obtenerListaProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PUT /api/productos/{id} - Actualizar producto exitosamente")
    void actualizarProductoTest() throws Exception {
        Categoriaid categoriaId = Categoriaid.generar();
        Categoria categoria = new Categoria(categoriaId, "Electrónicos", "Dispositivos electrónicos");

        //Primero creamos un producto para luego actualizarlo
        Productoid productoId = Productoid.generar();
        Producto producto = new Producto(productoId, "Monitor 4K", "Monitor de 27 pulgadas", Money.pesos(300), categoriaId);
        productoRepository.save(producto); // Guardamos el producto para que exista en la base de datos
        
        // Creamos un request con los nuevos datos para actualizar el producto
        ProductoRequest updateRequest = new ProductoRequest(
            "Monitor 4K Actualizado",
            "Monitor de 27 pulgadas con HDR",
            BigDecimal.valueOf(350.00),
            "MXN",
            categoriaId.getValue() // Usamos el mismo ID de categoría
        );

        // Ejecutamos la solicitud de actualización
        mockMvc.perform(put("/api/productos/{id}", productoId.getValue())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Monitor 4K Actualizado"))
                .andExpect(jsonPath("$.descripcion").value("Monitor de 27 pulgadas con HDR"))
                .andExpect(jsonPath("$.precio").value(350.00))
                .andExpect(jsonPath("$.moneda").value("MXN"));
    }
}
