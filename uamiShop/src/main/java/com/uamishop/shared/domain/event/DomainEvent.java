package com.uamishop.shared.domain.event;

import java.time.Instant;

/**
 * Base interface for all domain events.
 * Domain events represent something that happened in the domain that domain experts care about.
 */
public interface DomainEvent {
    
    /**
     * Returns the timestamp when the event occurred.
     * @return the instant when the event occurred
     */
    Instant occurredOn();
}
