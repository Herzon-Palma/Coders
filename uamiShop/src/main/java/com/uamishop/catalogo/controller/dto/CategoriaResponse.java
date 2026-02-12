package com.uamishop.catalogo.controller.dto;

import java.util.UUID;
import com.uamishop.catalogo.domain.Categoriaid;

public record CategoriaResponse(UUID id, String nombre, String descripcion, UUID categoriaid) {

}
