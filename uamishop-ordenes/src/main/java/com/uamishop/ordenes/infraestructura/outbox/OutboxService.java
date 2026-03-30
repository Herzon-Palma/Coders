package com.uamishop.ordenes.infraestructura.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void append(String aggregateType, UUID aggregateId, String eventType, String exchangeName, String routingKey, Object eventPayload) {
        try {
            String payloadStr = objectMapper.writeValueAsString(eventPayload);
            OutboxEvent event = OutboxEvent.pending(
                    aggregateType,
                    aggregateId,
                    eventType,
                    exchangeName,
                    routingKey,
                    payloadStr,
                    Instant.now()
            );
            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error seriliazando evento para outbox", e);
        }
    }
}
