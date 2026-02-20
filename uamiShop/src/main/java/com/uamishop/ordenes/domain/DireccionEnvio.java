package com.uamishop.ordenes.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record DireccionEnvio(
    String calle, 
    String ciudad, 
    String codigoPostal, 
    String pais
) {
    public DireccionEnvio {
        if (calle == null || calle.isBlank()) throw new IllegalArgumentException("Calle obligatoria");
        if (codigoPostal == null || codigoPostal.isBlank()) throw new IllegalArgumentException("CP obligatorio");
    }
}
