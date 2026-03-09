package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.Productoid;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Objects;

/**
 * Entity: línea de la orden con información histórica del producto al momento
 * de la compra.
 * El precio es inmutable (snapshot del momento de la compra).
 */
@Entity
@Table(name = "items_orden")
public class ItemOrden {

    @EmbeddedId
    private final ItemOrdenId id;

    private String nombreProducto;

    @Column(name = "cantidad_unidades") // Renombramos las unidades para estar seguros
    private BigDecimal cantidad;

    @Embedded
    @AttributeOverrides({
            // El 'name' es "cantidad" porque así se llama en tu record Money
            @AttributeOverride(name = "cantidad", column = @Column(name = "precio_unitario_monto")),
            @AttributeOverride(name = "moneda", column = @Column(name = "precio_unitario_divisa"))
    })
    private Money precioUnitario;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "productoid.valor", column = @Column(name = "producto_id")),
            @AttributeOverride(name = "nombreProducto", column = @Column(name = "producto_nombre")),
            @AttributeOverride(name = "sku", column = @Column(name = "producto_sku"))
    })
    private final ProductoRef productoRef;

    public ItemOrden(UUID productoId, String nombre, BigDecimal cantidad, Money precio) {
        this.id = ItemOrdenId.newId();
        this.nombreProducto = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precio;
        this.productoRef = null;
        this.subtotal = precio.multiplicar(cantidad);
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "subtotal_cantidad")),
            @AttributeOverride(name = "moneda", column = @Column(name = "subtotal_moneda"))
    })
    private final Money subtotal;

    protected ItemOrden() {
        this.id = null;
        this.productoRef = null;
        this.cantidad = null;
        this.precioUnitario = null;
        this.subtotal = null;
    }

    /**
     * Crea un item de orden con snapshot inmutable del producto.
     * El precio se recibe como parámetro separado porque ProductoRef
     * no contiene precio (enfoque DDD: el precio es del contexto de la compra).
     *
     * @param productoRef    referencia histórica del producto (ID, nombre, SKU)
     * @param cantidad       cantidad de unidades (debe ser > 0)
     * @param precioUnitario precio unitario al momento de la compra
     */
    public ItemOrden(ProductoRef productoRef, BigDecimal cantidad, Money precioUnitario) {
        Objects.requireNonNull(productoRef, "ProductoRef no puede ser null");
        Objects.requireNonNull(precioUnitario, "El precio unitario no puede ser null");
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        this.id = ItemOrdenId.newId();
        this.productoRef = productoRef;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nombreProducto = productoRef.nombreProducto();
        this.subtotal = precioUnitario.multiplicar(cantidad);
    }

    

    public Money calcularSubtotal() {
        return subtotal;
    }

    // --- Getters ---

    public ItemOrdenId getId() {
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

    // --- Igualdad por identidad ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemOrden that = (ItemOrden) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}