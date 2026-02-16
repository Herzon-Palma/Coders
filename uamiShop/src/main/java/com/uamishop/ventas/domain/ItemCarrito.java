package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    @Id
    private UUID id = UUID.randomUUID();
    
    private UUID productoId; //Referencia al Bounded Context de Catálogo
    private String nombreProducto;
    private int cantidad;
    
    @Embedded //JPA aplana los campos de Money
    private Money precioUnitario;

    protected ItemCarrito() {}

    public ItemCarrito(UUID productoId, String nombre, int cantidad, Money precio) {
        this.productoId = productoId;
        this.nombreProducto = nombre;
        this.precioUnitario = precio;
        setCantidad(cantidad); //Setter para validar
    }

    //Regla de negocio
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) throw new DomainException("La cantidad debe ser mayor a 0");
        this.cantidad = cantidad;
    }

    public void aumentarCantidad(int extra) {
        setCantidad(this.cantidad + extra);
    }

    public UUID getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public Money getPrecioUnitario() { return precioUnitario; }
    public String getNombreProducto() { return nombreProducto; }
}
