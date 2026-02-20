package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Entity: línea de la orden con información histórica del producto al momento
 * de la compra.
 * El precio es inmutable (snapshot del momento de la compra).
 */
@Entity
@Table(name = "items_orden")
public class ItemOrden {

    @EmbeddedId
    private final ItemOrdenId id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "sku", column = @Column(name = "producto_sku")),
        @AttributeOverride(name = "nombre", column = @Column(name = "producto_nombre")),
        @AttributeOverride(name = "precio.cantidad", column = @Column(name = "producto_precio_cantidad")),
        @AttributeOverride(name = "precio.moneda", column = @Column(name = "producto_precio_moneda"))
    })
    private final ProductoRef productoRef;

    private final int cantidad;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cantidad", column = @Column(name = "precio_unitario_cantidad")),
        @AttributeOverride(name = "moneda", column = @Column(name = "precio_unitario_moneda"))
    })
    private final Money precioUnitario;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cantidad", column = @Column(name = "subtotal_cantidad")),
        @AttributeOverride(name = "moneda", column = @Column(name = "subtotal_moneda"))
    })
    private final Money subtotal;

    protected ItemOrden() {
        this.id = null;
        this.productoRef = null;
        this.cantidad = 0;
        this.precioUnitario = null;
        this.subtotal = null;
    }

    /**
     * Crea un item de orden con snapshot inmutable del producto.
     *
     * @param productoRef referencia histórica del producto (SKU, nombre, precio)
     * @param cantidad    cantidad de unidades (debe ser > 0)
     */
    public ItemOrden(ProductoRef productoRef, int cantidad) {
        Objects.requireNonNull(productoRef, "ProductoRef no puede ser null");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        this.id = ItemOrdenId.newId();
        this.productoRef = productoRef;
        this.cantidad = cantidad;
        this.precioUnitario = productoRef.precio();
        this.subtotal = precioUnitario.multiplicar(cantidad);
    }

    public Money calcularSubtotal() {
        return subtotal;
    }

    // --- Getters ---

    public ItemOrdenId getId() {
        return id;
    }

    public ProductoRef getProductoRef() {
        return productoRef;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Money getPrecioUnitario() {
        return precioUnitario;
    }

    // --- Igualdad por identidad ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemOrden that = (ItemOrden) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
