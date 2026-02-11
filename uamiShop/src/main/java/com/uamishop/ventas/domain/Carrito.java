package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "carritos")
@Getter
public class Carrito {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private CarritoId id;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrito_id")
    private List<ItemCarrito> items;

    @Enumerated(EnumType.STRING)
    private EstadoCarrito estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    protected Carrito() {}

    public static Carrito crear(UUID clienteId) {
        Carrito c = new Carrito();
        c.id = CarritoId.random();
        c.clienteId = clienteId;
        c.items = new ArrayList<>();
        c.estado = EstadoCarrito.ACTIVO;
        c.fechaCreacion = LocalDateTime.now();
        c.fechaActualizacion = LocalDateTime.now();
        return c;
    }


    public void agregarProducto(ProductoRef producto, int cantidad) {
        validarEstadoActivo();
        
        Optional<ItemCarrito> itemExistente = items.stream()
                .filter(i -> i.getProductoRef().getProductoId().equals(producto.getProductoId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().incrementarCantidad(cantidad);
        } else {
            if (items.size() >= 20) throw new DomainException("Carrito lleno (max 20 items diferentes)");
            items.add(new ItemCarrito(producto, cantidad));
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void vaciar() {
        validarEstadoActivo();
        this.items.clear();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Money calcularTotal() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(Money.zero(), Money::sumar);
    }

    public void iniciarCheckout() {
        validarEstadoActivo();
        if (items.isEmpty()) throw new DomainException("El carrito está vacío");
        
        Money total = calcularTotal();
        if (!total.esMayorQue(Money.pesos(50))) {
            throw new DomainException("El total debe ser mayor a $50 para procesar la compra");
        }
        
        this.estado = EstadoCarrito.EN_CHECKOUT;
        this.fechaActualizacion = LocalDateTime.now();
    }

    private void validarEstadoActivo() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("El carrito no está activo. Estado actual: " + this.estado);
        }
    }
    
    // Getter defensivo
    public List<ItemCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }
}
