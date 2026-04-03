package com.uamishop.ventas.infraestructura;

import java.time.Instant;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uamishop.ventas.domain.OutboxEvent;
import com.uamishop.ventas.repository.OutboxEventRepository;

@Component
public class OutboxPublicador {
    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final int maxRetries = 3;

    public OutboxPublicador(OutboxEventRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Cada 5 segundos
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = repository.findPendingToPublish(
            Instant.now(), PageRequest.of(0, 50)
        );

        for (OutboxEvent event : events) {
            try {
                if (event.getAttemptCount() >= maxRetries) {
                    // Aquí podrías mover a una tabla de DLQ o notificar error crítico
                    continue;
                }

                // Convertir payload a mensaje Rabbit
                MessageProperties props = new MessageProperties();
                props.setContentType("application/json");
                Message message = new Message(event.getCarga().getBytes(), props);

                rabbitTemplate.send(event.getExchangeName(), event.getRoutingKey(), message);

                event.markAsSent();
                
            } catch (Exception e) {
                event.markAsFailed(e.getMessage());
            }
            repository.save(event);
        }
    }
}
