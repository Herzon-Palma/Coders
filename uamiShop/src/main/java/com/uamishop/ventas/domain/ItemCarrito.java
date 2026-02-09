package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import java.util.UUID;

public class ItemCarrito {
    private final UUID id;
    private final ProductoRef producto;
    private int cantidad;
    private Money precioUnitario;

    public ItemCarrito(ProductoRef producto, int cantidad, Money precioUnitario) {
        this.id = UUID.randomUUID();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public void aumentarCantidad(int cantidadExtra) {
        this.cantidad += cantidadExtra;
    }

    public void setCantidad(int nuevaCantidad) {
        this.cantidad = nuevaCantidad;
    }

    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public ProductoRef getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public Money getPrecioUnitario() { return precioUnitario; }
}
