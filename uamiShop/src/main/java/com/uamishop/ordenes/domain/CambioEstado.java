package com.uamishop.ordenes.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object: registro inmutable de un cambio de estado en la orden.
 * Cada transición se registra con estado anterior, nuevo, fecha, motivo y
 * usuario.
 */
public record CambioEstado(
        EstadoOrden estadoAnterior,
        EstadoOrden estadoNuevo,
        LocalDateTime fecha,
        String motivo,
        String usuario) {

    public CambioEstado {
        Objects.requireNonNull(estadoAnterior, "Estado anterior requerido");
        Objects.requireNonNull(estadoNuevo, "Estado nuevo requerido");
        Objects.requireNonNull(fecha, "Fecha requerida");
        Objects.requireNonNull(motivo, "Motivo requerido");
        Objects.requireNonNull(usuario, "Usuario requerido");
    }

    public String describir() {
        return String.format("%s → %s (%s) por %s el %s",
                estadoAnterior, estadoNuevo, motivo, usuario, fecha);
    }
}
