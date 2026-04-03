package com.uamishop.ventas.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uamishop.ventas.domain.Eventoprocesado;

public interface EventoProcesadoRepository extends JpaRepository<Eventoprocesado, UUID> {
    public boolean existsById(UUID eventId);
    
}
