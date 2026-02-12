package com.uamishop.catalogo.domain;

import java.util.UUID;

public record Productoid(UUID valor) {
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
