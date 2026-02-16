package com.uamishop.ventas.domain;

import java.util.Map;
import java.util.Set;

/**
 * Enum del dominio: estados del ciclo de vida de un Carrito.
 * Transiciones según diagrama de estados:
 * ACTIVO → EN_CHECKOUT
 * EN_CHECKOUT → COMPLETADO | ABANDONADO
 * ABANDONADO → ACTIVO (reactivar)
 * COMPLETADO → (estado final)
 */
public enum EstadoCarrito {

    ACTIVO,
    EN_CHECKOUT,
    COMPLETADO,
    ABANDONADO;

    private static final Map<EstadoCarrito, Set<EstadoCarrito>> TRANSICIONES = Map.of(
            ACTIVO, Set.of(EN_CHECKOUT),
            EN_CHECKOUT, Set.of(COMPLETADO, ABANDONADO),
            COMPLETADO, Set.of(),
            ABANDONADO, Set.of(ACTIVO));

    public boolean puedeTransicionarA(EstadoCarrito nuevo) {
        return TRANSICIONES.getOrDefault(this, Set.of()).contains(nuevo);
    }

    public boolean esEstadoFinal() {
        return this == COMPLETADO;
    }

    /**
     * Indica si el carrito permite modificaciones (agregar, modificar, eliminar,
     * vaciar).
     */
    public boolean permiteModificaciones() {
        return this == ACTIVO;
    }
}
