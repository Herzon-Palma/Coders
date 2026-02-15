package com.uamishop.ordenes.domain;

import java.util.Set;
import java.util.Map;

/**
 * Enum del dominio: estados del ciclo de vida de una Orden.
 * Las transiciones válidas se definen según el diagrama de estados.
 */
public enum EstadoOrden {

    PENDIENTE,
    CONFIRMADA,
    PAGO_PROCESADO,
    EN_PREPARACION,
    ENVIADA,
    EN_TRANSITO,
    ENTREGADA,
    CANCELADA;

    private static final Map<EstadoOrden, Set<EstadoOrden>> TRANSICIONES = Map.of(
            PENDIENTE, Set.of(CONFIRMADA, CANCELADA),
            CONFIRMADA, Set.of(PAGO_PROCESADO, CANCELADA),
            PAGO_PROCESADO, Set.of(EN_PREPARACION, CANCELADA),
            EN_PREPARACION, Set.of(ENVIADA, CANCELADA),
            ENVIADA, Set.of(EN_TRANSITO, ENTREGADA),
            EN_TRANSITO, Set.of(ENTREGADA),
            ENTREGADA, Set.of(),
            CANCELADA, Set.of());

    /**
     * Valida si la transición a {@code nuevo} es permitida desde este estado.
     */
    public boolean puedeTransicionarA(EstadoOrden nuevo) {
        return TRANSICIONES.getOrDefault(this, Set.of()).contains(nuevo);
    }

    public boolean esEstadoFinal() {
        return this == ENTREGADA || this == CANCELADA;
    }
}
