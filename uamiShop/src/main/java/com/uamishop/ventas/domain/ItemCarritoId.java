package com.uamishop.ventas.domain;

import java.util.UUID;

public record ItemCarritoId(UUID valor) {
    public static ItemCarritoId generar() {
        return new ItemCarritoId(UUID.randomUUID());
    }
}
