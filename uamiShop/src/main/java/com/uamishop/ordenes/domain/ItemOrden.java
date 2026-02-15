package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;

import java.util.Objects;

/**
 * Entity: línea de la orden con información histórica del producto al momento
 * de la compra.
 * El precio es inmutable (snapshot del momento de la compra).
 */
public class ItemOrden {

    private final ItemOrdenId id;
    private final ProductoRef productoRef;
    private final int cantidad;
    private final Money precioUnitario;
    private final Money subtotal;

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
