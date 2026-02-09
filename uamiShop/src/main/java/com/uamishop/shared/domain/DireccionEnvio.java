package com.uamishop.shared.domain;
import jakarta.persistence.Embeddable;

@Embeddable
public record DireccionEnvio( String calle, String numExt, String Colonia, String ciudad, String cp, String estado, String pais, String instrucciones) {
    public DireccionEnvio {
        if (calle == null || numExt == null || Colonia == null || ciudad == null || cp == null || estado == null || pais == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        if (calle.isBlank() || numExt.isBlank() || Colonia.isBlank() || ciudad.isBlank() || cp.isBlank() || estado.isBlank() || pais.isBlank()) {
            throw new IllegalArgumentException("Todos los campos deben contener información");
        }
        if (!cp.matches("\\d{5}")) { //cp. matches regex para validar que el código postal tenga exactamente 5 dígitos
            throw new IllegalArgumentException("El código postal debe contener 5 dígitos");
        }
        if (!pais.equals("México")) {
            throw new IllegalArgumentException("El país debe ser México");
        }
    }
}


