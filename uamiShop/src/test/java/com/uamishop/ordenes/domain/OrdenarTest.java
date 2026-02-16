package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.exception.OrdenException;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrdenTest {

    private Orden orden;
    private final ClienteId clienteId = new ClienteId(UUID.randomUUID());
    private final DireccionEnvio direccion = new DireccionEnvio("Calle 1", "CDMX", "09000", "Mexico");
    private final ItemOrden item = new ItemOrden(UUID.randomUUID(), "Laptop", 1, Money.of(15000));

    @BeforeEach
    void setup() {
        //Se ejecuta antes de cada test: Crea una orden nueva
        orden = new Orden(clienteId, direccion, List.of(item));
    }

    @Test
    @DisplayName("Debe crear una orden en estado CREADA")
    void crearOrden() {
        assertEquals(EstadoOrden.CREADA, orden.getEstado());
        assertNotNull(orden.getId());
    }

    @Test
    @DisplayName("Flujo exitoso: Pagar -> Enviar -> Entregar")
    void flujoCompleto() {
        //1. Pagar
        orden.pagar("REF-123", Money.of(15000));
        assertEquals(EstadoOrden.PAGADA, orden.getEstado());

        //2. Enviar
        orden.marcarEnviada();
        assertEquals(EstadoOrden.EN_TRANSITO, orden.getEstado());

        //3. Entregar
        orden.marcarEntregada();
        assertEquals(EstadoOrden.ENTREGADA, orden.getEstado());
    }

    @Test
    @DisplayName("Error: No se puede enviar una orden si no está pagada")
    void errorEnviarSinPagar() {
        //La orden nace CREADA
        assertThrows(OrdenException.class, () -> {
            orden.marcarEnviada();
        });
    }

    @Test
    @DisplayName("Error: No se puede cancelar una orden que ya va en camino")
    void errorCancelarEnTransito() {
        orden.pagar("REF-123", Money.of(15000));
        orden.marcarEnviada(); //Estado: EN_TRANSITO

        assertThrows(OrdenException.class, () -> {
            orden.cancelar("Ya no la quiero");
        });
    }

    @Test
    @DisplayName("Debe cancelar correctamente si está en estado CREADA")
    void cancelarOrden() {
        orden.cancelar("Me arrepentí");
        assertEquals(EstadoOrden.CANCELADA, orden.getEstado());
    }
}
