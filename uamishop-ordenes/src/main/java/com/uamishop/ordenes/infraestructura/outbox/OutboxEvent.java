package com.uamishop.ordenes.infraestructura.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    private String exchangeName;
    private String routingKey;
    private String payload;
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private int attemptCount;
    private Instant nextAttemptAt;
    private String lastError;
    private Instant sentAt;

    protected OutboxEvent() {}

    public static OutboxEvent pending(String aggregateType, UUID aggregateId, String eventType, String exchangeName, String routingKey, String payload, Instant occurredAt) {
        OutboxEvent event = new OutboxEvent();
        event.id = UUID.randomUUID();
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.eventType = eventType;
        event.exchangeName = exchangeName;
        event.routingKey = routingKey;
        event.payload = payload;
        event.occurredAt = occurredAt;
        event.status = OutboxStatus.PENDING;
        event.attemptCount = 0;
        event.nextAttemptAt = occurredAt;
        return event;
    }

    public void markSent(Instant sentAt) {
        this.status = OutboxStatus.SENT;
        this.sentAt = sentAt;
    }

    public void markFailed(String errorMessage, Instant nextAttemptAt) {
        this.status = OutboxStatus.FAILED;
        this.lastError = errorMessage;
        this.attemptCount++;
        this.nextAttemptAt = nextAttemptAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getExchangeName() { return exchangeName; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }
    public OutboxStatus getStatus() { return status; }
    public int getAttemptCount() { return attemptCount; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public String getLastError() { return lastError; }
    public Instant getSentAt() { return sentAt; }
}
