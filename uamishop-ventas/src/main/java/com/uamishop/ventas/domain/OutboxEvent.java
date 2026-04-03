package com.uamishop.ventas.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.uamishop.ventas.domain.OutboxStatus;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;
    private String aggregateType; // Ej: "ORDEN", "CARRITO"
    private UUID aggregateId;
    private String eventType;    // Ej: "OrdenCreadaEvent"
    private String exchangeName;
    private String routingKey;

    @Column(columnDefinition = "TEXT")
    private String carga;     // JSON del evento

    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private int attemptCount; // Para llevar control de reintentos
    private Instant nextAttemptAt;
    private String lastError;
    private Instant sentAt;

    public OutboxEvent() {
        // Constructor vacío para JPA
    }

    public static OutboxEvent pending(String aggregateType, UUID aggregateId, String eventType, 
                                    String exchange, String routingKey, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.id = UUID.randomUUID();
        event.createdAt = Instant.now();
        event.status = OutboxStatus.PENDIENTE;
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.eventType = eventType;
        event.exchangeName = exchange;
        event.routingKey = routingKey;
        event.carga = payload;
        event.attemptCount = 0;
        event.nextAttemptAt = Instant.now(); // Listo para procesar ya
        return event;
    }

    public void markAsSent() {
        this.status = OutboxStatus.PROCESADO;
        this.sentAt = Instant.now();
    }
    
    public void markAsFailed(String error) {
        this.status = OutboxStatus.ERROR;
        this.lastError = error;
        this.attemptCount += 1;
        // Exponencial backoff para el próximo intento
        this.nextAttemptAt = Instant.now().plusSeconds((long) Math.pow(2, attemptCount));
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getExchangeName() { return exchangeName; }
    public String getRoutingKey() { return routingKey; }
    public String getCarga() { return carga; }
    public Instant getCreatedAt() { return createdAt; }
    public OutboxStatus getStatus() { return status; }
    public int getAttemptCount() { return attemptCount; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public String getLastError() { return lastError; }
    public Instant getSentAt() { return sentAt; }
}
