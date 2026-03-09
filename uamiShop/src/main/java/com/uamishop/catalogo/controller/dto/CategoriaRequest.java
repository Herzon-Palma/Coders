package com.uamishop.catalogo.controller.dto;

import jakarta.validation.constraints.*;

public record CategoriaRequest(@NotEmpty String nombre, @NotEmpty String descripcion) {

}
