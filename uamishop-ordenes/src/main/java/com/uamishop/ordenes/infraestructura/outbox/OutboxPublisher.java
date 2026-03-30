package com.uamishop.ordenes.infraestructura.outbox;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final int maxRetries = 3;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, RabbitTemplate rabbitTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop50ByStatusInAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                        List.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
                        Instant.now()
                );

        for (OutboxEvent event : events) {
            if (event.getAttemptCount() >= maxRetries) {
                continue; // Can be marked as permanently failed or handled differently
            }

            try {
                // Publish JSON payload string to RabbitMQ
                rabbitTemplate.convertAndSend(
                        event.getExchangeName(),
                        event.getRoutingKey(),
                        event.getPayload()
                );
                event.markSent(Instant.now());
            } catch (Exception e) {
                long backoffSeconds = (long) Math.pow(2, event.getAttemptCount());
                event.markFailed(e.getMessage(), Instant.now().plus(backoffSeconds, ChronoUnit.SECONDS));
            }
            outboxEventRepository.save(event);
        }
    }
}
