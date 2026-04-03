package com.uamishop.ordenes.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uamishop.ordenes.domain.OutboxEvent;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query("SELECT e FROM OutboxEvent e WHERE e.status IN ('PENDING', 'FAILED') " +
           "AND e.nextAttemptAt <= :now ORDER BY e.occurredAt ASC")
    List<OutboxEvent> findPendingToPublish(@Param("now") Instant now, Pageable pageable);
}
