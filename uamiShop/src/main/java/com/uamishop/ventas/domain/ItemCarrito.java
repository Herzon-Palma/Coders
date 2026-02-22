package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.exception.CarritoException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    
    @EmbeddedId
    private final ItemCarritoId id;

    @Embedded
    private final ProductoRef productoRef;

    private BigDecimal cantidad;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "precio_unitario_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "precio_unitario_currency"))
    }) // Utilizamos AttributeOverrides para mapear los campos de Money a columnas específicas en la tabla de items
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
