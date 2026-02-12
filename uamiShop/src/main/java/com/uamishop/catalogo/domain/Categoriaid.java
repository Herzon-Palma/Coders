package com.uamishop.catalogo.domain;

import java.util.UUID;

public record Categoriaid(UUID valor) {
    public static Categoriaid generar() {
        return new Categoriaid(UUID.randomUUID());
    }

}
