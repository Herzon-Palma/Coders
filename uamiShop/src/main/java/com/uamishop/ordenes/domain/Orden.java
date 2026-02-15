package com.uamishop.ordenes.domain;

import com.uamishop.ordenes.domain.exception.ReglaNegocioException;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.shared.domain.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root: Orden.
 * Representa una transacción de compra confirmada. Gestiona el ciclo de vida
 * desde creación hasta entrega, coordina items, envío y pago, y mantiene
 * historial de cambios de estado.
 *
 * Reglas: RN-ORD-01 a RN-ORD-16.
 */
public class Orden {

    private final OrdenId id;
    private final String numeroOrden;
    private final ClienteId clienteId;
    private final List<ItemOrden> items;
    private final DireccionEnvio direccionEnvio;
    private ResumenPago resumenPago;
    private InfoEnvio infoEnvio;
    private final Money subtotal;
    private final Money descuento;
    private final Money total;
    private EstadoOrden estado;
    private final LocalDateTime fechaCreacion;
    private final List<CambioEstado> historialEstados;

    // --- Constructor privado ---

    private Orden(OrdenId id, String numeroOrden, ClienteId clienteId,
            List<ItemOrden> items, DireccionEnvio direccionEnvio,
            ResumenPago resumenPago, Money subtotal, Money descuento,
            Money total, EstadoOrden estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.numeroOrden = numeroOrden;
        this.clienteId = clienteId;
        this.items = new ArrayList<>(items);
        this.direccionEnvio = direccionEnvio;
        this.resumenPago = resumenPago;
        this.infoEnvio = null;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.historialEstados = new ArrayList<>();
    }

    // ========================================================================
    // FACTORY METHOD
    // ========================================================================

    /**
     * Crea una nueva Orden en estado PENDIENTE.
     *
     * @throws ReglaNegocioException si se viola alguna invariante (RN-ORD-01..04)
     */
    public static Orden crear(ClienteId clienteId, List<ItemOrden> items,
            DireccionEnvio direccion, ResumenPago pago) {
        Objects.requireNonNull(clienteId, "ClienteId no puede ser null");
        Objects.requireNonNull(items, "Items no puede ser null");
        Objects.requireNonNull(direccion, "DireccionEnvio no puede ser null");
        Objects.requireNonNull(pago, "ResumenPago no puede ser null");

        // RN-ORD-01: al menos un item
        if (items.isEmpty()) {
            throw new ReglaNegocioException("RN-ORD-01",
                    "Una orden debe tener al menos un item");
        }

        // Calcular totales
        Money subtotal = items.stream()
                .map(ItemOrden::calcularSubtotal)
                .reduce(Money.zero(), Money::sumar);

        Money descuento = Money.zero();
        Money total = subtotal.restar(descuento);

        // RN-ORD-02: total > 0
        if (total.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("RN-ORD-02",
                    "El total de la orden debe ser mayor a cero");
        }

        // RN-ORD-03 y RN-ORD-04 se validan dentro del constructor de DireccionEnvio

        String numeroOrden = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new Orden(
                OrdenId.newId(), numeroOrden, clienteId,
                items, direccion, pago,
                subtotal, descuento, total,
                EstadoOrden.PENDIENTE, LocalDateTime.now());
    }

    // ========================================================================
    // TRANSICIONES DE ESTADO
    // ========================================================================

    /**
     * Confirma la orden. Solo válido desde PENDIENTE.
     * - RN-ORD-05: Solo se puede confirmar en estado PENDIENTE.
     * - RN-ORD-06: Se registra el cambio en historial.
     */
    public void confirmar() {
        // RN-ORD-05
        validarTransicion(EstadoOrden.CONFIRMADA, "RN-ORD-05",
                "Solo se puede confirmar una orden en estado PENDIENTE");

        // RN-ORD-06
        cambiarEstado(EstadoOrden.CONFIRMADA, "Orden confirmada");
    }

    /**
     * Procesa el pago de la orden. Solo válido desde CONFIRMADA.
     * - RN-ORD-07: Solo se puede procesar pago si la orden está CONFIRMADA.
     * - RN-ORD-08: La referencia de pago no puede estar vacía.
     */
    public void procesarPago(String referenciaPago) {
        // RN-ORD-07
        validarTransicion(EstadoOrden.PAGO_PROCESADO, "RN-ORD-07",
                "Solo se puede procesar pago si la orden está CONFIRMADA");

        // RN-ORD-08
        if (referenciaPago == null || referenciaPago.isBlank()) {
            throw new ReglaNegocioException("RN-ORD-08",
                    "La referencia de pago no puede estar vacía");
        }

        this.resumenPago = this.resumenPago.conPagoAprobado(referenciaPago);
        cambiarEstado(EstadoOrden.PAGO_PROCESADO, "Pago procesado: " + referenciaPago);
    }

    /**
     * Marca la orden en proceso de preparación. Solo válido desde PAGO_PROCESADO.
     * - RN-ORD-09: Solo se puede marcar en proceso si el pago fue procesado.
     */
    public void marcarEnProceso() {
        // RN-ORD-09
        validarTransicion(EstadoOrden.EN_PREPARACION, "RN-ORD-09",
                "Solo se puede marcar en proceso si el pago fue procesado");

        cambiarEstado(EstadoOrden.EN_PREPARACION, "Orden en preparación");
    }

    /**
     * Marca la orden como enviada. Solo válido desde EN_PREPARACION.
     * - RN-ORD-10: Solo puede marcarse enviada si está EN_PREPARACION.
     * - RN-ORD-11: Debe proporcionarse el número de guía.
     * - RN-ORD-12: El número de guía debe tener al menos 10 caracteres.
     */
    public void marcarEnviada(InfoEnvio infoEnvio) {
        // RN-ORD-10
        validarTransicion(EstadoOrden.ENVIADA, "RN-ORD-10",
                "Solo puede marcarse enviada si está EN_PREPARACION");

        // RN-ORD-11, RN-ORD-12: se validan en el constructor de InfoEnvio
        Objects.requireNonNull(infoEnvio, "InfoEnvio no puede ser null");

        this.infoEnvio = infoEnvio;
        cambiarEstado(EstadoOrden.ENVIADA,
                "Enviada con guía: " + infoEnvio.numeroGuia());
    }

    /**
     * Marca la orden como entregada. Solo válido desde ENVIADA o EN_TRANSITO.
     * - RN-ORD-13: Solo se puede marcar entregada si está ENVIADA o EN_TRANSITO.
     */
    public void marcarEntregada() {
        // RN-ORD-13
        if (this.estado != EstadoOrden.ENVIADA && this.estado != EstadoOrden.EN_TRANSITO) {
            throw new ReglaNegocioException("RN-ORD-13",
                    "Solo se puede marcar entregada si está ENVIADA o EN_TRANSITO");
        }

        cambiarEstado(EstadoOrden.ENTREGADA, "Orden entregada al cliente");
    }

    /**
     * Cancela la orden con un motivo.
     * - RN-ORD-14: No se puede cancelar si ya está ENVIADA, EN_TRANSITO, ENTREGADA
     * o CANCELADA.
     * - RN-ORD-15: Debe proporcionarse motivo de cancelación.
     * - RN-ORD-16: El motivo debe tener al menos 10 caracteres.
     */
    public void cancelar(String motivo) {
        // RN-ORD-14
        if (!this.estado.puedeTransicionarA(EstadoOrden.CANCELADA)) {
            throw new ReglaNegocioException("RN-ORD-14",
                    "No se puede cancelar una orden en estado " + this.estado);
        }

        // RN-ORD-15
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaNegocioException("RN-ORD-15",
                    "Debe proporcionarse motivo de cancelación");
        }

        // RN-ORD-16
        if (motivo.trim().length() < 10) {
            throw new ReglaNegocioException("RN-ORD-16",
                    "El motivo debe tener al menos 10 caracteres");
        }

        cambiarEstado(EstadoOrden.CANCELADA, motivo);
    }

    // ========================================================================
    // CONSULTAS
    // ========================================================================

    public EstadoOrden obtenerEstadoActual() {
        return estado;
    }

    public List<CambioEstado> obtenerHistorial() {
        return Collections.unmodifiableList(historialEstados);
    }

    // ========================================================================
    // MÉTODOS PRIVADOS
    // ========================================================================

    private void validarTransicion(EstadoOrden estadoDestino, String reglaId, String mensaje) {
        if (!this.estado.puedeTransicionarA(estadoDestino)) {
            throw new ReglaNegocioException(reglaId, mensaje + " (estado actual: " + this.estado + ")");
        }
    }

    private void cambiarEstado(EstadoOrden nuevoEstado, String motivo) {
        EstadoOrden anterior = this.estado;
        this.estado = nuevoEstado;
        this.historialEstados.add(new CambioEstado(
                anterior, nuevoEstado, LocalDateTime.now(), motivo, "sistema"));
    }

    // ========================================================================
    // GETTERS (sin setters — encapsulación)
    // ========================================================================

    public OrdenId getId() {
        return id;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public List<ItemOrden> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DireccionEnvio getDireccionEnvio() {
        return direccionEnvio;
    }

    public ResumenPago getResumenPago() {
        return resumenPago;
    }

    public InfoEnvio getInfoEnvio() {
        return infoEnvio;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public Money getDescuento() {
        return descuento;
    }

    public Money getTotal() {
        return total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // --- Igualdad por identidad ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Orden orden = (Orden) o;
        return Objects.equals(id, orden.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
