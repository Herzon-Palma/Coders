package com.uamishop.ventas.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.ventas.domain.OutboxEvent;
import com.uamishop.ventas.repository.OutboxEventRepository;



@Service
public class OutboxService {
    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY) // Exige que ya haya una transacción de negocio
    public void appendEvent(String aggregateType, UUID aggregateId, String exchange, 
                            String routingKey, Object eventPayload) {
        try {
            String payload = objectMapper.writeValueAsString(eventPayload);
            String eventType = eventPayload.getClass().getSimpleName();
            
            OutboxEvent outbox = OutboxEvent.pending(
                aggregateType, aggregateId, eventType, exchange, routingKey, payload
            );
            
            repository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento", e);
        }
    }
}
