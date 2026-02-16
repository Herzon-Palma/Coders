package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.domain.exception.ReglaNegocioVentasException;

import java.util.Objects;

/**
 * Entity: línea del carrito con un producto, cantidad y precio unitario.
 * Tiene identidad propia dentro del carrito.
 */
public class ItemCarrito {

    private static final int CANTIDAD_MAXIMA = 10;

    private final ItemCarritoId id;
    private final ProductoRef productoRef;
    private int cantidad;
    private final Money precioUnitario;

    public ItemCarrito(ProductoRef productoRef, int cantidad, Money precioUnitario) {
        Objects.requireNonNull(productoRef, "ProductoRef no puede ser null");
        Objects.requireNonNull(precioUnitario, "Precio unitario no puede ser null");
        validarCantidad(cantidad);

        this.id = ItemCarritoId.generar();
        this.productoRef = productoRef;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Reemplaza la cantidad del item.
     * - RN-VEN-05: La nueva cantidad debe ser mayor a cero.
     * - RN-VEN-02: La cantidad máxima por producto es 10.
     */
    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new ReglaNegocioVentasException("RN-VEN-05",
                    "La cantidad debe ser mayor a cero; para eliminar use eliminarProducto()");
        }
        if (nuevaCantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioVentasException("RN-VEN-02",
                    "La cantidad máxima por producto es " + CANTIDAD_MAXIMA);
        }
        this.cantidad = nuevaCantidad;
    }

    /**
     * Incrementa la cantidad del item.
     * - RN-VEN-02: El total no puede superar 10 unidades.
     */
    public void incrementarCantidad(int cantidadAdicional) {
        if (cantidadAdicional <= 0) {
            throw new ReglaNegocioVentasException("RN-VEN-01",
                    "La cantidad a agregar debe ser mayor a cero");
        }
        int nuevaCantidad = this.cantidad + cantidadAdicional;
        if (nuevaCantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioVentasException("RN-VEN-02",
                    "La cantidad máxima por producto es " + CANTIDAD_MAXIMA
                            + " (actual: " + this.cantidad + ", solicitado: +" + cantidadAdicional + ")");
        }
        this.cantidad = nuevaCantidad;
    }

    public Money calcularSubtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    // --- Getters ---

    public ItemCarritoId getId() {
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

    // --- Privados ---

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioVentasException("RN-VEN-01",
                    "La cantidad debe ser mayor a cero");
        }
        if (cantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioVentasException("RN-VEN-02",
                    "La cantidad máxima por producto es " + CANTIDAD_MAXIMA);
        }
    }

    // --- Igualdad por identidad ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemCarrito that = (ItemCarrito) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
