package com.uamishop.ventas.domain;

import java.util.Objects;
import java.util.regex.Pattern;
import com.uamishop.shared.domain.Money;

import com.uamishop.catalogo.domain.Productoid;
import com.uamishop.shared.domain.exception.DomainException;

/**
 * Value Object compartido: referencia histórica de producto.
 * Regla (Práctica 2):
 * - RN-VO-05: SKU debe tener formato AAA-000 (3 letras mayúsculas, guion, 3
 * dígitos).
 */

public record ProductoRef(Productoid productoid, String nombreProducto, String sku, Money precio) {
    private static final Pattern PATRON_SKU = Pattern.compile("^[A-Z]{3}-\\d{3}$");

    public ProductoRef {
        Objects.requireNonNull(sku, "El productRef SKU no puede ser nulo");
        Objects.requireNonNull(nombreProducto, "El productRef nombre no puede ser nulo");
        Objects.requireNonNull(precio, "El productRef precio no puede ser nulo");
        sku = sku.trim().toUpperCase();
        nombreProducto = nombreProducto.trim();
        if (!PATRON_SKU.matcher(sku).matches()) {
            throw new DomainException("El productRef SKU debe tener formato AAA-000"); // RN-VO-05
        }

        nombreProducto = nombreProducto.trim();
        if (nombreProducto.isEmpty() || nombreProducto.isBlank()) {
            throw new DomainException("El productRef nombre no puede estar vacío");
        }
    }

    public ProductoRef(Productoid productoid, String nombreProducto, String sku, double precio) {
        this(productoid, nombreProducto, sku, Money.pesos(precio));
    }

    

}
