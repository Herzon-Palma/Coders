package com.uamishop.ventas.domain;

import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable
public record ItemCarritoId(UUID valor) {
    public static ItemCarritoId generar() {
        return new ItemCarritoId(UUID.randomUUID());
    }
}
