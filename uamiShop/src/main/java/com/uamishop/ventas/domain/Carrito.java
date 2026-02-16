package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "carritos")
public class Carrito {
    @Id
    private UUID id = UUID.randomUUID();
    
    private UUID clienteId;
    
    //asegura que si guardo Carrito se guardan los items
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrito_id") 
    private List<ItemCarrito> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoCarrito estado;
    
    private String estado; //ACTIVO, CHECKOUT

    protected Carrito() {}

    public Carrito(UUID clienteId) {
        this.clienteId = clienteId;
        this.estado = EstadoCarrito.ACTIVO;
    }

    //LÓGICA DE NEGOCIO (DDD)
    public void agregarProducto(UUID productoId, String nombre, int cantidad, Money precio) {
        verificarEstadoEditable();
        
        Optional<ItemCarrito> existente = items.stream()
            .filter(i -> i.getProductoId().equals(productoId))
            .findFirst();

        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
        } else {
            items.add(new ItemCarrito(productoId, nombre, cantidad, precio));
        }
    }

    public void modificarCantidad(UUID productoId, int nuevaCantidad) {
        verificarEstadoEditable();
        items.stream()
            .filter(i -> i.getProductoId().equals(productoId))
            .findFirst()
            .ifPresentOrElse(
                item -> item.setCantidad(nuevaCantidad),
                () -> { throw new DomainException("Producto no encontrado en el carrito"); }
            );
    }

    public void eliminarProducto(UUID productoId) {
        verificarEstadoEditable();
        boolean removed = items.removeIf(i -> i.getProductoId().equals(productoId));
        if (!removed) throw new DomainException("El producto no estaba en el carrito");
    }

    public void iniciarCheckout() {
        if (items.isEmpty()) throw new DomainException("No se puede hacer checkout de un carrito vacío");
        this.estado = EstadoCarrito.CHECKOUT;
    }

    private void verificarEstadoEditable() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("El carrito ya no puede ser modificado");
        }
    }

    //Getters
    public UUID getId() { return id; }
    public List<ItemCarrito> getItems() { return items; }
    public String getEstado() { return estado; }
    public EstadoCarrito getEstado() { return estado; }
}
