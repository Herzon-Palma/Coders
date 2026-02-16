package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.exception.CarritoException;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    
    @Id
    @GeneratedValue
    private Long id; 

    @Embedded
    private ProductoRef producto;

    private int cantidad;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "precio_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "precio_currency"))
    })
    private Money precioUnitario;

    protected ItemCarrito() {}

    public ItemCarrito(ProductoRef producto, int cantidad, Money precio) {
        this.producto = producto;
        this.precioUnitario = precio;
        setCantidad(cantidad);
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) throw new CarritoException("Cantidad debe ser positiva");
        this.cantidad = cantidad;
    }
    
    public void aumentarCantidad(int n) { this.cantidad += n; }

    public ProductoRef getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public Money getPrecioUnitario() { return precioUnitario; }
}
