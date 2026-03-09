package com.uamishop.ordenes.domain;

/**
 * Enum del dominio: estados del pago sincronizado con el contexto de Pagos.
 */
public enum EstadoPago {
    PENDIENTE,
    PROCESANDO,
    APROBADO,
    RECHAZADO
}
