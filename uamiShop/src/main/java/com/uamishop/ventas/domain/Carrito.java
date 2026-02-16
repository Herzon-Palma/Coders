package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.domain.exception.ReglaNegocioVentasException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate Root: Carrito de compras.
 * Gestiona la colección de productos que un cliente desea comprar.
 * Controla operaciones de agregar, modificar y eliminar items.
 * Coordina checkout y calcula totales con descuentos aplicados.
 *
 * Reglas: RN-VEN-01 a RN-VEN-16.
 */
public class Carrito {

    private static final int MAX_PRODUCTOS_DIFERENTES = 20;
    private static final BigDecimal MONTO_MINIMO_CHECKOUT = new BigDecimal("50");
    private static final BigDecimal PORCENTAJE_MAXIMO_DESCUENTO = new BigDecimal("30");

    private final CarritoId id;
    private final ClienteId clienteId;
    private final List<ItemCarrito> items;
    private final List<DescuentoAplicado> descuentos;
    private EstadoCarrito estado;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // --- Constructor privado ---

    private Carrito(CarritoId id, ClienteId clienteId) {
        this.id = id;
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    // ========================================================================
    // FACTORY METHOD
    // ========================================================================

    /**
     * Crea un nuevo Carrito vacío en estado ACTIVO.
     */
    public static Carrito crear(ClienteId clienteId) {
        Objects.requireNonNull(clienteId, "ClienteId no puede ser null");
        return new Carrito(CarritoId.generar(), clienteId);
    }

    // ========================================================================
    // OPERACIONES CON ITEMS
    // ========================================================================

    /**
     * Agrega un producto al carrito o incrementa la cantidad si ya existe.
     * - RN-VEN-01: La cantidad debe ser mayor a cero.
     * - RN-VEN-02: La cantidad máxima por producto es 10.
     * - RN-VEN-03: Máximo 20 productos diferentes.
     * - RN-VEN-04: Si el producto ya existe, se suma la cantidad.
     */
    public void agregarProducto(ProductoRef productoRef, int cantidad, Money precio) {
        validarEstadoParaModificacion("agregar productos");
        Objects.requireNonNull(productoRef, "ProductoRef no puede ser null");
        Objects.requireNonNull(precio, "Precio no puede ser null");

        // RN-VEN-01
        if (cantidad <= 0) {
            throw new ReglaNegocioVentasException("RN-VEN-01",
                    "La cantidad debe ser mayor a cero");
        }

        // RN-VEN-04: si ya existe, incrementar cantidad
        Optional<ItemCarrito> existente = buscarItemPorSku(productoRef.sku());
        if (existente.isPresent()) {
            existente.get().incrementarCantidad(cantidad); // RN-VEN-02 se valida dentro
            actualizarFecha();
            return;
        }

        // RN-VEN-03: máximo 20 productos diferentes
        if (items.size() >= MAX_PRODUCTOS_DIFERENTES) {
            throw new ReglaNegocioVentasException("RN-VEN-03",
                    "Un carrito puede tener máximo " + MAX_PRODUCTOS_DIFERENTES + " productos diferentes");
        }

        // RN-VEN-02 se valida en el constructor de ItemCarrito
        items.add(new ItemCarrito(productoRef, cantidad, precio));
        actualizarFecha();
    }

    /**
     * Modifica la cantidad de un producto existente.
     * - RN-VEN-05: La nueva cantidad debe ser mayor a cero.
     * - RN-VEN-06: No se puede modificar si el carrito está en checkout.
     */
    public void modificarCantidad(String sku, int nuevaCantidad) {
        // RN-VEN-06
        validarEstadoParaModificacion("modificar cantidad");

        ItemCarrito item = buscarItemPorSkuObligatorio(sku);
        item.actualizarCantidad(nuevaCantidad); // RN-VEN-05 se valida dentro
        actualizarFecha();
    }

    /**
     * Elimina un producto del carrito.
     * - RN-VEN-07: No se puede eliminar si el carrito está en checkout.
     * - RN-VEN-08: Debe existir el producto en el carrito.
     */
    public void eliminarProducto(String sku) {
        // RN-VEN-07
        validarEstadoParaModificacion("eliminar productos");

        // RN-VEN-08
        ItemCarrito item = buscarItemPorSkuObligatorio(sku);
        items.remove(item);
        actualizarFecha();
    }

    /**
     * Vacía todos los items del carrito.
     * - RN-VEN-09: No se puede vaciar un carrito en checkout.
     */
    public void vaciar() {
        // RN-VEN-09
        validarEstadoParaModificacion("vaciar el carrito");
        items.clear();
        descuentos.clear();
        actualizarFecha();
    }

    // ========================================================================
    // CÁLCULOS
    // ========================================================================

    /**
     * Calcula el subtotal (suma de subtotales de cada item, sin descuentos).
     */
    public Money calcularSubtotal() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(Money.zero(), Money::sumar);
    }

    /**
     * Calcula el total aplicando los descuentos al subtotal.
     */
    public Money calcularTotal() {
        Money subtotal = calcularSubtotal();

        Money totalDescuentos = descuentos.stream()
                .map(d -> d.calcularDescuento(subtotal))
                .reduce(Money.zero(), Money::sumar);

        // Evitar total negativo
        if (totalDescuentos.cantidad().compareTo(subtotal.cantidad()) >= 0) {
            return Money.zero();
        }

        return subtotal.restar(totalDescuentos);
    }

    public int obtenerCantidadItems() {
        return items.size();
    }

    // ========================================================================
    // DESCUENTOS
    // ========================================================================

    /**
     * Aplica un descuento al carrito.
     * - RN-VEN-15: Solo un cupón de descuento por carrito.
     * - RN-VEN-16: El descuento no puede ser mayor al 30% del subtotal.
     */
    public void aplicarDescuento(DescuentoAplicado descuento) {
        validarEstadoParaModificacion("aplicar descuentos");
        Objects.requireNonNull(descuento, "Descuento no puede ser null");

        // RN-VEN-15
        if (!descuentos.isEmpty()) {
            throw new ReglaNegocioVentasException("RN-VEN-15",
                    "Solo se puede aplicar un cupón de descuento por carrito");
        }

        // RN-VEN-16: validar que el descuento no exceda el 30% del subtotal
        Money subtotal = calcularSubtotal();
        Money montoDescuento = descuento.calcularDescuento(subtotal);
        BigDecimal porcentajeDescuento = montoDescuento.cantidad()
                .multiply(new BigDecimal("100"))
                .divide(subtotal.cantidad(), 2, java.math.RoundingMode.HALF_UP);

        if (porcentajeDescuento.compareTo(PORCENTAJE_MAXIMO_DESCUENTO) > 0) {
            throw new ReglaNegocioVentasException("RN-VEN-16",
                    "El descuento no puede ser mayor al 30% del subtotal");
        }

        descuentos.add(descuento);
        actualizarFecha();
    }

    // ========================================================================
    // TRANSICIONES DE ESTADO
    // ========================================================================

    /**
     * Inicia el proceso de checkout.
     * - RN-VEN-10: El carrito debe tener al menos un producto.
     * - RN-VEN-11: El carrito debe estar ACTIVO.
     * - RN-VEN-12: El total debe ser mayor a $50 MXN.
     */
    public void iniciarCheckout() {
        // RN-VEN-11
        if (!estado.puedeTransicionarA(EstadoCarrito.EN_CHECKOUT)) {
            throw new ReglaNegocioVentasException("RN-VEN-11",
                    "El carrito debe estar ACTIVO para iniciar checkout (estado actual: " + estado + ")");
        }

        // RN-VEN-10
        if (items.isEmpty()) {
            throw new ReglaNegocioVentasException("RN-VEN-10",
                    "El carrito debe tener al menos un producto para iniciar checkout");
        }

        // RN-VEN-12
        Money total = calcularTotal();
        if (total.cantidad().compareTo(MONTO_MINIMO_CHECKOUT) < 0) {
            throw new ReglaNegocioVentasException("RN-VEN-12",
                    "El total del carrito debe ser mayor a $" + MONTO_MINIMO_CHECKOUT + " pesos");
        }

        this.estado = EstadoCarrito.EN_CHECKOUT;
        actualizarFecha();
    }

    /**
     * Completa el checkout.
     * - RN-VEN-13: Solo se puede completar si está EN_CHECKOUT.
     */
    public void completarCheckout() {
        // RN-VEN-13
        if (!estado.puedeTransicionarA(EstadoCarrito.COMPLETADO)) {
            throw new ReglaNegocioVentasException("RN-VEN-13",
                    "Solo se puede completar el checkout si está EN_CHECKOUT (estado actual: " + estado + ")");
        }
        this.estado = EstadoCarrito.COMPLETADO;
        actualizarFecha();
    }

    /**
     * Abandona el checkout, volviendo items a un estado de espera.
     * - RN-VEN-14: Solo se puede abandonar si está EN_CHECKOUT.
     */
    public void abandonar() {
        // RN-VEN-14
        if (!estado.puedeTransicionarA(EstadoCarrito.ABANDONADO)) {
            throw new ReglaNegocioVentasException("RN-VEN-14",
                    "Solo se puede abandonar si está EN_CHECKOUT (estado actual: " + estado + ")");
        }
        this.estado = EstadoCarrito.ABANDONADO;
        actualizarFecha();
    }

    /**
     * Reactiva un carrito abandonado, volviéndolo a estado ACTIVO.
     * (Transición del diagrama de estados: ABANDONADO → ACTIVO)
     */
    public void reactivar() {
        if (!estado.puedeTransicionarA(EstadoCarrito.ACTIVO)) {
            throw new ReglaNegocioVentasException("RN-VEN-REACT",
                    "Solo se puede reactivar un carrito ABANDONADO (estado actual: " + estado + ")");
        }
        this.estado = EstadoCarrito.ACTIVO;
        actualizarFecha();
    }

    // ========================================================================
    // CONSULTAS
    // ========================================================================

    public EstadoCarrito obtenerEstadoActual() {
        return estado;
    }

    // ========================================================================
    // MÉTODOS PRIVADOS
    // ========================================================================

    private void validarEstadoParaModificacion(String operacion) {
        if (!estado.permiteModificaciones()) {
            throw new ReglaNegocioVentasException("RN-VEN-06",
                    "No se puede " + operacion + " cuando el carrito está en estado " + estado);
        }
    }

    private Optional<ItemCarrito> buscarItemPorSku(String sku) {
        return items.stream()
                .filter(item -> item.getProductoRef().sku().equals(sku))
                .findFirst();
    }

    private ItemCarrito buscarItemPorSkuObligatorio(String sku) {
        return buscarItemPorSku(sku)
                .orElseThrow(() -> new ReglaNegocioVentasException("RN-VEN-08",
                        "No existe un producto con SKU '" + sku + "' en el carrito"));
    }

    private void actualizarFecha() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ========================================================================
    // GETTERS (sin setters — encapsulación)
    // ========================================================================

    public CarritoId getId() {
        return id;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public List<ItemCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<DescuentoAplicado> getDescuentos() {
        return Collections.unmodifiableList(descuentos);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    // --- Igualdad por identidad ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Carrito carrito = (Carrito) o;
        return Objects.equals(id, carrito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
