package com.uamishop.catalogo.controller.dto;
import java.math.BigDecimal;
import java.util.UUID;


public record ProductoResponse(UUID id, String nombre, String descripcion, BigDecimal precio, String moneda, UUID categoriaid) {

}
