package com.uamishop.catalogo.domain;

import java.util.UUID;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record Categoriaid(UUID valor) implements Serializable {
    public static Categoriaid generar() {
        return new Categoriaid(UUID.randomUUID());
    }

    public static Categoriaid of(String id) {
        return new Categoriaid(UUID.fromString(id));
    }

    public UUID getValue() {
        return valor;
    }

    
}
