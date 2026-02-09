package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrito {
    private final CarritoId id;
    private final String clienteId;
    private List<ItemCarrito> items;
    private EstadoCarrito estado;
    private LocalDateTime fechaCreacion;

    public Carrito(String clienteId) {
        this.id = CarritoId.generar();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void agregarProducto(ProductoRef producto, int cantidad, Money precio) {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("El carrito no esta activo");
        }

        if (cantidad <= 0) {
            throw new DomainException("La cantidad debe ser mayor a 0");
        }

        Optional<ItemCarrito> itemOpt = buscarItem(producto.getProductoId());
        int cantidadActual = itemOpt.map(ItemCarrito::getCantidad).orElse(0);

        if (cantidadActual + cantidad > 10) {
            throw new DomainException("Maximo 10 unidades por producto");
        }

        if (itemOpt.isPresent()) {
            itemOpt.get().aumentarCantidad(cantidad);
        } else {
            if (items.size() >= 20) {
                throw new DomainException("Limite de 20 productos diferentes alcanzado");
            }
            items.add(new ItemCarrito(producto, cantidad, precio));
        }
    }

    public void cambiarCantidad(String productoId, int nuevaCantidad) {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("No se puede modificar en checkout");
        }
        
        if (nuevaCantidad <= 0) {
            throw new DomainException("Use eliminarProducto para quitar items");
        }
        
        if (nuevaCantidad > 10) {
            throw new DomainException("Maximo 10 unidades permitidas");
        }

        buscarItem(productoId).ifPresent(item -> item.setCantidad(nuevaCantidad));
    }

    public void eliminarProducto(String productoId) {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("Carrito bloqueado");
        }
        
        ItemCarrito item = buscarItem(productoId)
            .orElseThrow(() -> new DomainException("El producto no está en el carrito"));
        
        items.remove(item);
    }

    public void vaciar() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("No se puede vaciar durante el checkout");
        }
        items.clear();
    }

    public void checkout() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("El estado debe ser ACTIVO");
        }
        
        if (items.isEmpty()) {
            throw new DomainException("El carrito está vacío");
        }

        Money total = total();
        if (total.esMenorQue(new Money(50))) {
            throw new DomainException("El monto mínimo es $50");
        }

        this.estado = EstadoCarrito.EN_CHECKOUT;
    }

    public void finalizarCompra() {
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new DomainException("Estado incorrecto para finalizar");
        }
        this.estado = EstadoCarrito.COMPLETADO;
    }

    public void cancelarCheckout() {
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new DomainException("Solo se puede cancelar durante checkout");
        }
        this.estado = EstadoCarrito.ABANDONADO;
    }

    public Money total() {
        return items.stream()
                .map(ItemCarrito::subtotal)
                .reduce(new Money(0), Money::sumar);
    }

    private Optional<ItemCarrito> buscarItem(String pid) {
        return items.stream()
                .filter(i -> i.getProducto().getProductoId().equals(pid))
                .findFirst();
    }
    
    public CarritoId getId() { return id; }
    public EstadoCarrito getEstado() { return estado; }
    public List<ItemCarrito> getItems() { return new ArrayList<>(items); }
}
