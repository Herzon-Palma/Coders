package com.uamishop.shared.domain;

import java.util.Objects;
import java.util.regex.Pattern;
import com.uamishop.shared.domain.exception.DomainException;

/**
 * Value Object compartido: referencia histórica de producto.
 * Regla (Práctica 2):
 * - RN-VO-05: SKU debe tener formato AAA-000 (3 letras mayúsculas, guion, 3
 * dígitos).
 */

public record ProductoRef(String sku, String nombre, Money precio) {
    private static final Pattern PATRON_SKU = Pattern.compile("^[A-Z]{3}-\\d{3}$");

    public ProductoRef {
        Objects.requireNonNull(sku, "El productRef SKU no puede ser nulo");
        Objects.requireNonNull(nombre, "El productRef nombre no puede ser nulo");
        Objects.requireNonNull(precio, "El productRef precio no puede ser nulo");
        sku = sku.trim().toUpperCase();
        nombre = nombre.trim();
        if (!PATRON_SKU.matcher(sku).matches()) {
            throw new DomainException("El productRef SKU debe tener formato AAA-000"); // RN-VO-05
        }

        nombre = nombre.trim();
        if (nombre.isEmpty() || nombre.isBlank()) {
            throw new DomainException("El productRef nombre no puede estar vacío");
        }
    }

}
