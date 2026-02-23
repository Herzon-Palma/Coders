package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.exception.DomainException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Está clase representa el aggregate root de niestro diagrama 
   de clases del documento de la practica 2*/
@Entity
@Table(name = "carritos")
public class Carrito {

    @EmbeddedId
    private final CarritoId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "cliente_id", nullable = false)) // el nullable nos sirve
                                                                                             // para que JPA no intente
                                                                                             // crear una tabla aparte
                                                                                             // para ClienteId
    private final ClienteId clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) // el orphanRemoval asegura que si un ItemCarrito se
                                                                // elimina de la lista, también se borre de la base de
                                                                // datos
    @JoinColumn(name = "carrito_id") // Clave foránea en la tabla de items que referencia al carrito
    private final List<ItemCarrito> items;

    @ElementCollection // Para almacenar los descuentos aplicados como una colección de elementos
                       // embebidos
    @CollectionTable(name = "descuentos_carrito", joinColumns = @JoinColumn(name = "carrito_id")) // Tabla para los
                                                                                                  // descuentos
                                                                                                  // aplicados
    private final List<DescuentoAplicado> descuentos;

    @Enumerated(EnumType.STRING) // Guardamos el estado como texto en la base de datos para mayor claridad
    private EstadoCarrito estado;

    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor sin argumentos requerido por JPA
    protected Carrito() {
        this.id = null;
        this.clienteId = null;
        this.items = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    private Carrito(ClienteId clienteId) {
        this.id = CarritoId.generar();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public static Carrito crear(ClienteId clienteId) {
        return new Carrito(clienteId);
    }

    public void agregarProducto(ProductoRef productoRef, int cantidad, Money precio) {
        validarModificable();
        if (cantidad <= 0)
            throw new DomainException("La cantidad debe ser mayor a cero"); // RN-VEN-01

        Optional<ItemCarrito> itemExistente = items.stream()
                .filter(i -> i.getProductoRef().productoid().equals(productoRef.productoid()))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            if (item.getCantidad().add(BigDecimal.valueOf(cantidad)).compareTo(BigDecimal.valueOf(10)) > 0) {
                throw new DomainException("La cantidad máxima por producto es 10"); // RN-VEN-02
            }
            item.incrementarCantidad(cantidad); // RN-VEN-04
        } else {
            if (items.size() >= 20)
                throw new DomainException("Un carrito puede tener máximo 20 productos diferentes"); // RN-VEN-03
            if (cantidad > 10)
                throw new DomainException("La cantidad máxima por producto es 10"); // RN-VEN-02
            items.add(new ItemCarrito(productoRef, cantidad, precio));
        }
        actualizarFecha();
    }

    public void modificarCantidad(Productoid productoid, int nuevaCantidad) {
        validarModificable(); // RN-VEN-06
        if (nuevaCantidad <= 0)
            throw new DomainException("La nueva cantidad debe ser mayor a cero"); // RN-VEN-05
        if (nuevaCantidad > 10)
            throw new DomainException("La cantidad máxima por producto es 10"); // RN-VEN-02

        ItemCarrito item = buscarItem(productoid);
        item.actualizarCantidad(nuevaCantidad);
        actualizarFecha();
    }

    public void eliminarProducto(Productoid productoid) {
        validarModificable(); // RN-VEN-07
        ItemCarrito item = buscarItem(productoid); // Lanza excepción si no existe, cumple RN-VEN-08
        items.remove(item);
        actualizarFecha();
    }

    public void vaciar() {
        validarModificable(); // RN-VEN-09
        items.clear();
        descuentos.clear();
        actualizarFecha();
    }

    public void iniciarCheckout() {
        if (this.estado != EstadoCarrito.ACTIVO)
            throw new DomainException("El carrito debe estar ACTIVO"); // RN-VEN-11
        if (this.items.isEmpty())
            throw new DomainException("El carrito debe tener al menos un producto"); // RN-VEN-10
        if (calcularTotal().cantidad().compareTo(BigDecimal.valueOf(50)) < 0)
            throw new DomainException("El total debe ser mayor a $50 pesos"); // RN-VEN-12

        this.estado = EstadoCarrito.CHECKOUT;
        actualizarFecha();
    }

    public void completarCheckout() {
        if (this.estado != EstadoCarrito.CHECKOUT)
            throw new DomainException("Solo se puede completar si está EN_CHECKOUT"); // RN-VEN-13
        this.estado = EstadoCarrito.COMPLETADO;
        actualizarFecha();
    }

    public void abandonar() {
        if (this.estado != EstadoCarrito.CHECKOUT)
            throw new DomainException("Solo se puede abandonar si está EN_CHECKOUT"); // RN-VEN-14
        this.estado = EstadoCarrito.ABANDONADO;
        actualizarFecha();
    }

    public void aplicarDescuento(DescuentoAplicado descuento) {
        validarModificable();
        if (!descuentos.isEmpty())
            throw new DomainException("Solo se puede aplicar un cupón por carrito"); // RN-VEN-15

        Money limiteDescuento = calcularSubtotal().multiplicar(BigDecimal.valueOf(0.3)); // 30% del subtotal
        if (descuento.montoDescuento().cantidad().compareTo(limiteDescuento.cantidad()) > 0) {
            throw new DomainException("El descuento no puede ser mayor al 30% del subtotal"); // RN-VEN-16
        }

        descuentos.add(descuento);
        actualizarFecha();
    }

    public Money calcularSubtotal() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(Money::sumar)
                .orElse(Money.zero());
    }

    public Money calcularTotal() {
        Money subtotal = calcularSubtotal();
        Money totalDescuentos = descuentos.stream()
                .map(DescuentoAplicado::montoDescuento)
                .reduce(Money::sumar)
                .orElse(Money.zero());
        return subtotal.restar(totalDescuentos);
    }

    public Integer obtenerCantidadItems() {
        return items.stream()
                .map(ItemCarrito::getCantidad)
                .mapToInt(BigDecimal::intValue) // Convertimos cada BigDecimal a entero
                .sum();
    }

    // Funciones de apoyo
    private void validarModificable() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new DomainException("No se permiten modificaciones al carrito en estado: " + this.estado);
        }
    }

    private ItemCarrito buscarItem(Productoid productoid) {
        return items.stream()
                .filter(i -> i.getProductoRef().productoid().equals(productoid))
                .findFirst()
                .orElseThrow(() -> new DomainException("El producto no existe en el carrito"));
    }

    private void actualizarFecha() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters para exponer datos a la base de datos má
    public CarritoId getId() {
        return id;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public List<DescuentoAplicado> getDescuentos() {
        return descuentos;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

}
