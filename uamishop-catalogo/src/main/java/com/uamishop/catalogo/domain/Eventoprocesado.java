package com.uamishop.catalogo.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;

@Entity
@Table(name = "eventos_procesados")
public class Eventoprocesado { 
    
    @Id
    private UUID eventId; // El ID que viene del evento
    private Instant processedAt;
    private String handlerName; // Útil si varios métodos escuchan el mismo evento

    public Eventoprocesado() {}

    public Eventoprocesado(UUID eventId, String handlerName) {
        this.eventId = eventId;
        this.handlerName = handlerName;
        this.processedAt = Instant.now();
    }

}
