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
    @Query("SELECT e FROM OutboxEvent e WHERE e.status IN (com.uamishop.ordenes.domain.OutboxStatus.PENDIENTE, com.uamishop.ordenes.domain.OutboxStatus.ERROR) " +
           "AND e.nextAttemptAt <= :now ORDER BY e.createdAt ASC")
    List<OutboxEvent> findPendingToPublish(@Param("now") Instant now, Pageable pageable);
}
