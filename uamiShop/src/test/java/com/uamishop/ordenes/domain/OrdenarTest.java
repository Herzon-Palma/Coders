package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.exception.ReglaNegocioException;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrdenarTest {

    private Orden orden;
    private final ClienteId clienteId = new ClienteId(UUID.randomUUID());
    private DireccionEnvio direccion;
    private ItemOrden item;
    private ResumenPago pagoInicial;

    @BeforeEach
    void setup() {
        direccion = new DireccionEnvio(
            "Juan Perez", "Av. Siempre Viva 123", "Springfield", "EdoMex", 
            "12345", "México", "5512345678", "Casa blanca"
        );
        
        ProductoRef prodRef = new ProductoRef("SKU-123", "Laptop", Money.of(15000));
        item = new ItemOrden(prodRef, 1);
        
        pagoInicial = ResumenPago.crear("TARJETA");

        // Se ejecuta antes de cada test: Crea una orden nueva (PENDIENTE)
        orden = Orden.crear(clienteId, List.of(item), direccion, pagoInicial);
    }

    @Test
    @DisplayName("Debe crear una orden en estado PENDIENTE")
    void crearOrden() {
        assertEquals(EstadoOrden.PENDIENTE, orden.obtenerEstadoActual());
        assertNotNull(orden.getId());
    }

    @Test
    @DisplayName("Flujo exitoso: Confirmar -> Pagar -> Preparar -> Enviar -> Entregar")
    void flujoCompleto() {
        // 1. Confirmar
        orden.confirmar();
        assertEquals(EstadoOrden.CONFIRMADA, orden.obtenerEstadoActual());

        // 2. Pagar
        orden.procesarPago("REF-PAGO-123");
        assertEquals(EstadoOrden.PAGO_PROCESADO, orden.obtenerEstadoActual());

        // 3. Preparar
        orden.marcarEnProceso();
        assertEquals(EstadoOrden.EN_PREPARACION, orden.obtenerEstadoActual());

        // 4. Enviar
        InfoEnvio info = new InfoEnvio("DHL", "GUIA-1234567890", LocalDateTime.now().plusDays(2));
        orden.marcarEnviada(info);
        assertEquals(EstadoOrden.ENVIADA, orden.obtenerEstadoActual());

        // 5. Entregar
        orden.marcarEntregada();
        assertEquals(EstadoOrden.ENTREGADA, orden.obtenerEstadoActual());
    }

    @Test
    @DisplayName("Error: No se puede enviar una orden si no está en preparación")
    void errorEnviarSinPreparar() {
        // Estado PENDIENTE
        InfoEnvio info = new InfoEnvio("DHL", "GUIA-1234567890", LocalDateTime.now().plusDays(2));
        
        assertThrows(ReglaNegocioException.class, () -> {
            orden.marcarEnviada(info);
        });
    }

    @Test
    @DisplayName("Error: No se puede cancelar una orden que ya fue enviada")
    void errorCancelarEnviada() {
        orden.confirmar();
        orden.procesarPago("REF-123");
        orden.marcarEnProceso(); 
        orden.marcarEnviada(new InfoEnvio("DHL", "GUIA-1234567890", LocalDateTime.now().plusDays(2)));

        assertThrows(ReglaNegocioException.class, () -> {
            orden.cancelar("Ya no la quiero");
        });
    }

    @Test
    @DisplayName("Debe cancelar correctamente si está en estado PENDIENTE")
    void cancelarOrden() {
        orden.cancelar("Me arrepentí");
        assertEquals(EstadoOrden.CANCELADA, orden.obtenerEstadoActual());
    }
}
