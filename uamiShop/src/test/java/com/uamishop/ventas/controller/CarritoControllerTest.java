package com.uamishop.ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc; //Simula peticiones HTTP sin abrir navegador

    @MockBean
    private CarritoService carritoService; //Simulamos el servicio (Mock)

    @Autowired
    private ObjectMapper objectMapper; //Convierte objetos a JSON

    @Test
    void debeCrearUObtenerCarrito() throws Exception {
        UUID clienteUuid = UUID.randomUUID();
        Carrito carritoSimulado = new Carrito(new ClienteId(clienteUuid));

        when(carritoService.obtenerCarritoActivo(any(UUID.class))).thenReturn(carritoSimulado);

        mockMvc.perform(get("/api/carritos")
                .param("clienteId", clienteUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void debeAgregarProducto() throws Exception {
        UUID carritoId = UUID.randomUUID();
        
        //Creamos el DTO de entrada
        AgregarProductoRequest request = new AgregarProductoRequest(
                UUID.randomUUID(), 
                "Laptop Gamer", 
                1, 
                25000.00
        );

        //Simulamos respuesta exitosa
        when(carritoService.agregarProducto(eq(carritoId), any(), any(), any(Integer.class), any(Double.class)))
                .thenReturn(new Carrito(new ClienteId(UUID.randomUUID()))); //Retorna cualquier carrito válido

        mockMvc.perform(post("/api/carritos/{id}/productos", carritoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) //Convierte objeto Java a JSON
                .andExpect(status().isOk());
    }
}
