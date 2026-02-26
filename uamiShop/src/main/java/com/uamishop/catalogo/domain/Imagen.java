package com.uamishop.catalogo.domain;

import java.util.Objects;
import jakarta.persistence.Embeddable;


@Embeddable
public record Imagen(String url, String altText, Integer orden) {
    public Imagen {
        
        if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            throw new IllegalArgumentException("La URL de la imagen debe ser válida (http/https)"); 
        }
        Objects.requireNonNull(altText, "El texto alternativo es obligatorio");
        Objects.requireNonNull(orden, "El orden es obligatorio");
    }
}
