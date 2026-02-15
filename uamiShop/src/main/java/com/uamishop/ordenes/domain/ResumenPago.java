package com.uamishop.ordenes.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object: resumen del pago asociado a la orden.
 * Referencia al contexto de Pagos sin acoplamiento directo.
 */
public record ResumenPago(
        String metodoPago,
        String referenciaExterna,
        EstadoPago estado,
        LocalDateTime fechaProcesamiento) {

    public ResumenPago {
        Objects.requireNonNull(metodoPago, "Método de pago requerido");
        Objects.requireNonNull(estado, "Estado de pago requerido");

        if (metodoPago.isBlank()) {
            throw new IllegalArgumentException("Método de pago no puede estar vacío");
        }
    }

    /**
     * Factory method: crea un resumen en estado PENDIENTE.
     */
    public static ResumenPago crear(String metodoPago) {
        return new ResumenPago(metodoPago, null, EstadoPago.PENDIENTE, null);
    }

    /**
     * Devuelve un nuevo ResumenPago con el pago aprobado.
     * 
     * @param referenciaExterna referencia del procesador de pagos
     */
    public ResumenPago conPagoAprobado(String referenciaExterna) {
        Objects.requireNonNull(referenciaExterna, "Referencia de pago requerida");
        if (referenciaExterna.isBlank()) {
            throw new IllegalArgumentException("Referencia de pago no puede estar vacía");
        }
        return new ResumenPago(this.metodoPago, referenciaExterna,
                EstadoPago.APROBADO, LocalDateTime.now());
    }
}
