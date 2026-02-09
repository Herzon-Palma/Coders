package com.uamishop.ordenes.domain.valueobject;

/**
 * Estados del pago dentro de una orden.
 * Máquina de estados según State_Payment.puml.
 */
public enum PaymentStatus {
    PENDIENTE,
    APROBADO,
    RECHAZADO
}
