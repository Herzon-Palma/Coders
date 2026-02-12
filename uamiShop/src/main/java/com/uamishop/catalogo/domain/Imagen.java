package com.uamishop.catalogo.domain;

import java.util.Objects;

public record Imagen(String url, String altText, Integer orden) {
    public Imagen {
        // RN-CAT-07: La URL debe empezar con http:// o https://
        if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            throw new IllegalArgumentException("La URL de la imagen debe ser válida (http/https)"); 
        }
        Objects.requireNonNull(altText, "El texto alternativo es obligatorio");
        Objects.requireNonNull(orden, "El orden es obligatorio");
    }
}
