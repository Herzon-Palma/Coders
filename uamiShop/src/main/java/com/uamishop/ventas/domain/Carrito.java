package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.exception.CarritoException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "carritos")
public class Carrito {

    @EmbeddedId
    private CarritoId id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cliente_id"))
    })
    private ClienteId clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrito_id") 
    private List<ItemCarrito> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoCarrito estado;

    @Embedded
    private DescuentoAplicado descuento;

    protected Carrito() {}

    public Carrito(ClienteId clienteId) {
        this.id = new CarritoId();
        this.clienteId = clienteId;
        this.estado = EstadoCarrito.ACTIVO;
    }

    public void agregarProducto(ProductoRef prodRef, int cantidad, Money precio) {
        if (this.estado != EstadoCarrito.ACTIVO) throw new CarritoException("Carrito cerrado");

        Optional<ItemCarrito> existente = items.stream()
            .filter(i -> i.getProducto().productoId().equals(prodRef.productoId()))
            .findFirst();

        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
        } else {
            items.add(new ItemCarrito(prodRef, cantidad, precio));
        }
    }
    
    public void iniciarCheckout() {
        this.estado = EstadoCarrito.CHECKOUT;
    }

    public CarritoId getId() { return id; }
    public EstadoCarrito getEstado() { return estado; }
    public List<ItemCarrito> getItems() { return items; }
}
