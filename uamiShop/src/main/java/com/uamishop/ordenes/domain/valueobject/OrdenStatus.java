package com.uamishop.ordenes.domain.valueobject;

/**
 * Estados de la orden.
 * Máquina de estados según State_Orden.puml.
 */
public enum OrdenStatus {
    PENDIENTE,
    CONFIRMADA,
    PAGADA,
    EN_PREPARACION,
    ENVIADA,
    ENTREGADA,
    CANCELADA
}
