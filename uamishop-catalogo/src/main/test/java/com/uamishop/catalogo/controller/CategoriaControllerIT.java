package com.uamishop.catalogo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import com.uamishop.catalogo.repository.CategoriaRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc // Configura MockMvc para pruebas de integración
@Transactional
public class CategoriaControllerIT {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper; // Para convertir objetos a JSON
        @Autowired
        private CategoriaController categoriaController;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Test
        @DisplayName("Crear categoría exitosamente")
        void crearCategoriaTest() throws Exception {
                // Aquí iría la lógica para crear una categoría usando MockMvc y verificar la
                // respuesta
                CategoriaRequest request = new CategoriaRequest(
                                "Electrónicos",
                                "Dispositivos electrónicos de todo tipo");

                // Ejecutamos y verificamos
                mockMvc.perform(post("/api/v1/categorias")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nombre").value("Electrónicos"))
                                .andExpect(jsonPath("$.descripcion").value("Dispositivos electrónicos de todo tipo"));
        }

        @Test
        @DisplayName("Actualizar una categoría existente")
        void actualizarCategoriaTest() throws Exception {
                // Primero creamos una categoría para asegurarnos de que existe
                Categoriaid categoriaId = Categoriaid.generar();
                Categoria categoria = new Categoria(categoriaId, "Electrónicos", "Dispositivos electrónicos");
                categoriaRepository.save(categoria);

                CategoriaRequest request = new CategoriaRequest(
                                "Electrónicos Actualizados",
                                "Dispositivos electrónicos actualizados de todo tipo");

                mockMvc.perform(put("/api/v1/categorias/" + categoriaId.getValue())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Electrónicos Actualizados"))
                                .andExpect(jsonPath("$.descripcion")
                                                .value("Dispositivos electrónicos actualizados de todo tipo"));
        }
}
