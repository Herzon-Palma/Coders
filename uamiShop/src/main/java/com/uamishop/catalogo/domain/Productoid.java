package com.uamishop.catalogo.domain;

import java.util.UUID;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record Productoid(UUID valor) implements Serializable {
    public static Productoid generar() {
        return new Productoid(UUID.randomUUID());
    }

    public static Productoid of(String id) {
        return new Productoid(UUID.fromString(id));
    }

    public UUID getValue() {
        return valor;
    }
}
