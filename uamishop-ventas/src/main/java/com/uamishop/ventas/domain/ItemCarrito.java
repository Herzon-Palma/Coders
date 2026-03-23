package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    @EmbeddedId
    // Renombramos el campo 'valor' del ID del Item
    @AttributeOverride(name = "valor", column = @Column(name = "item_carrito_id"))
    private ItemCarritoId id;

    @Embedded
    @AttributeOverrides({
            // IMPORTANTE: Aquí renombramos el 'valor' que viene dentro de ProductoRef ->
            // Productoid
            // Usamos la notación de punto para llegar al campo interno
            @AttributeOverride(name = "productoId.id", column = @Column(name = "producto_id")),
            @AttributeOverride(name = "nombreProducto", column = @Column(name = "producto_nombre")),
            @AttributeOverride(name = "sku", column = @Column(name = "producto_sku"))
    })
    private ProductoRef productoRef;

    @Column(name = "cantidad_items")
    private BigDecimal cantidad;

    // 2. El dinero
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "precio_monto")), // 'cantidad' es el campo de
                                                                                            // Money
            @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private Money precioUnitario;

    // Constructor para JPA
    protected ItemCarrito() {
    }

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

    // Getters
    public ItemCarritoId getId() {
        return id;
    }

    public ProductoRef getProductoRef() {
        return productoRef;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public Money getPrecioUnitario() {
        return precioUnitario;
    }
}
