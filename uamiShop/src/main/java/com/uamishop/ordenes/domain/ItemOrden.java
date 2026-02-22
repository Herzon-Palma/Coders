package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "items_orden")
public class ItemOrden {
    @Id
    @GeneratedValue
    private Long id;

    private UUID productoId;
    private String nombreProducto;
    private BigDecimal cantidad;
    
    @Embedded
    private Money precioUnitario;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "subtotal_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "subtotal_currency"))
    })
    private Money subtotal;

    protected ItemOrden() {}

    public ItemOrden(UUID productoId, String nombre, BigDecimal cantidad, Money precio) {
        this.productoId = productoId;
        this.nombreProducto = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precio;
        this.subtotal = precio.multiplicar(cantidad);
    }
    
}
