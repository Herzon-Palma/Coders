package com.uamishop.ordenes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.ordenes.domain.DireccionEnvio;
import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.ClienteId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdenService ordenService;

    @Test
    @DisplayName("POST /api/ordenes - Debe crear orden correctamente")
    void crearOrden() throws Exception {
        //Datos de entrada
        CrearOrdenRequest request = new CrearOrdenRequest(
                UUID.randomUUID(),
                new DireccionEnvio("Av Siempre Viva", "Springfield", "12345", "USA"),
                List.of(new OrdenService.ItemDto(UUID.randomUUID(), "Producto X", 1, 100.0))
        );

        //Simulamos que el servicio responde una orden válida
        Orden ordenSimulada = new Orden(new ClienteId(UUID.randomUUID()), request.direccion(), List.of());
        when(ordenService.crearOrden(any(), any(), any())).thenReturn(ordenSimulada);

        mockMvc.perform(post("/api/ordenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/ordenes/{id}/pagar - Debe procesar pago")
    void pagarOrden() throws Exception {
        UUID ordenId = UUID.randomUUID();
        String referencia = "PAYPAL-12345";
        
        //Simulamos respuesta del servicio
        Orden ordenSimulada = new Orden(new ClienteId(UUID.randomUUID()), null, List.of());
        when(ordenService.pagarOrden(eq(ordenId), any(String.class))).thenReturn(ordenSimulada);

        mockMvc.perform(post("/api/ordenes/{id}/pagar", ordenId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(referencia))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("POST /api/ordenes/{id}/enviar - Debe marcar como enviada")
    void enviarOrden() throws Exception {
        UUID ordenId = UUID.randomUUID();
        Orden ordenSimulada = new Orden(new ClienteId(UUID.randomUUID()), null, List.of());
        
        when(ordenService.enviarOrden(ordenId)).thenReturn(ordenSimulada);

        mockMvc.perform(post("/api/ordenes/{id}/enviar", ordenId))
                .andExpect(status().isOk());
    }
}
