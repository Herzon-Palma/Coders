package com.uamishop.checkout.domain;

/**
 * Estados del proceso de checkout.
 * Máquina de estados según State_Checkout.puml.
 */
public enum EstadoCheckout {
    INICIADO,
    DATOS_CAPTURADOS,
    STOCK_VALIDADO,
    PAGO_APROBADO,
    ORDEN_CREADA,
    FALLIDO,
    CANCELADO
}
