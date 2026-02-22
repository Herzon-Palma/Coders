package com.uamishop.ventas.domain;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable
public record CarritoId(UUID id) implements Serializable {
    public static CarritoId generar() {
        return new CarritoId(UUID.randomUUID());
    }
    
    public UUID getValue() {
        return id;
    }

    
}
