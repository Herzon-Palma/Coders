package com.uamishop.ventas.controller.dto;

import java.util.UUID;

public record CarritoRequest( UUID productoId, Integer cantidad) {

}
