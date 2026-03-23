package com.uamishop.shared.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.Objects;
import java.util.regex.Pattern;
import com.uamishop.shared.domain.exception.DomainException;

/**
 * Value Object compartido: referencia histórica de producto.
 * Según diagrama SubDominio_ventas.puml:
 * - productId : ProductoId (Productoid)
 * - nombreProducto : String
 * - sku : String
 * 
 * Regla (Práctica 2):
 * - RN-VO-05: SKU debe tener formato AAA-000 (3 letras mayúsculas, guion, 3
 * dígitos).
 * 
 * No incluye precio: el precio es responsabilidad de la entidad que usa esta
 * referencia
 * (ItemCarrito, ItemOrden), ya que puede variar según el contexto y momento de
 * la compra.
 */

@Embeddable
public class ProductoRef {
    private static final Pattern PATRON_SKU = Pattern.compile("^[A-Z]{3}-\\d{3}$");

    @Embedded
    @jakarta.persistence.AttributeOverride(name = "valor", column = @jakarta.persistence.Column(name = "producto_id"))
    private Productoid productoid;

    private String nombreProducto;
    private String sku;

    protected ProductoRef() {
    }

    public ProductoRef(Productoid productoid, String nombreProducto, String sku) {
        Objects.requireNonNull(productoid, "El productRef ProductoId no puede ser nulo");
        Objects.requireNonNull(sku, "El productRef SKU no puede ser nulo");
        Objects.requireNonNull(nombreProducto, "El productRef nombre no puede ser nulo");
        this.sku = sku.trim().toUpperCase();
        this.nombreProducto = nombreProducto.trim();
        if (!PATRON_SKU.matcher(this.sku).matches()) {
            throw new DomainException("El productRef SKU debe tener formato AAA-000"); // RN-VO-05
        }
        if (this.nombreProducto.isEmpty() || this.nombreProducto.isBlank()) {
            throw new DomainException("El productRef nombre no puede estar vacío");
        }
        this.productoid = productoid;
    }

    public Productoid getProductId() {
        return productoid;
    }

    public Productoid productoid() {
        return productoid;
    }

    public String nombreProducto() {
        return nombreProducto;
    }

    public String sku() {
        return sku;
    }
}
