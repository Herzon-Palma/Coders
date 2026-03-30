package com.uamishop.ordenes.infraestructura.outbox;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED
}
