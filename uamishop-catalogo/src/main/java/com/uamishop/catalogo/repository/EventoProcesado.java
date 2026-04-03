package com.uamishop.catalogo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uamishop.catalogo.domain.Eventoprocesado;

public interface EventoProcesado extends JpaRepository<Eventoprocesado, UUID> {
    public boolean existsById(UUID eventId);
}
