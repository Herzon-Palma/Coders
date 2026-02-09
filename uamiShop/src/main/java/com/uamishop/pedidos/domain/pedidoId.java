package com.uamishop.pedidos.domain;

public class pedidoId {
    private final String value;
    
    public pedidoId(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo o vacío");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
