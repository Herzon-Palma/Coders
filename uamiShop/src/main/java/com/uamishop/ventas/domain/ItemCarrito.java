package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.domain.exception.CarritoException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;


public class ItemCarrito {
    
    private final ItemCarritoId id;
    private final ProductoRef productoRef;
    private BigDecimal cantidad;
    private final Money precioUnitario;

    public ItemCarrito(ProductoRef productoRef, int cantidad, Money precioUnitario) {
        this.id = ItemCarritoId.generar();
        this.productoRef = productoRef;
        this.cantidad = BigDecimal.valueOf(cantidad);
        this.precioUnitario = precioUnitario;
    }

    public void actualizarCantidad(int nuevaCantidad) {
        this.cantidad = BigDecimal.valueOf(nuevaCantidad);
    }

    public void incrementarCantidad(int cantidadAdicional) {
        this.cantidad = this.cantidad.add(BigDecimal.valueOf(cantidadAdicional));
    }

    public Money calcularSubtotal() {
        return this.precioUnitario.multiplicar(this.cantidad);
    }

    // Getters necesarios
    public ItemCarritoId getId() { return id; }
    public ProductoRef getProductoRef() { return productoRef; }
    public BigDecimal getCantidad() { return cantidad; }
    public Money getPrecioUnitario() { return precioUnitario; }
}
