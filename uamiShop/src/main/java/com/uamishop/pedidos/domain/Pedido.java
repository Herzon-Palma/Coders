package com.uamishop.pedidos.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.uamishop.identidad.domain.Usuario;
import com.uamishop.shared.domain.DomainException;
import com.uamishop.shared.domain.Producto;
import com.uamishop.shared.domain.Money;

public class Pedido {

    private final UUID id;
    private final String folio;
    private final LocalDateTime fechaCreacion;

    private float total;
    private Edo estado;

    private final Usuario usuario;
    private final List<DetallePedido> detalles;

    private Pago pago;
    private Envio envio;

   
    public Pedido(Usuario usuario, List<DetallePedido> detalles) {
        if (usuario == null) {
            throw new DomainException("El pedido debe tener un usuario");
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new DomainException("RN-PED-01: El pedido debe tener al menos un detalle");
        }

        this.id = UUID.randomUUID();
        this.folio = generarFolio();
        this.fechaCreacion = LocalDateTime.now();
        this.usuario = usuario;
        this.detalles = new ArrayList<>(detalles);
        this.estado = Edo.PENDIENTE;
        this.total = calcularTotal();

        if (this.total <= 0) {
            throw new DomainException("RN-PED-02: El total del pedido debe ser mayor a cero");
        }
    }

   
    public void confirmar() {
        if (estado != Edo.PENDIENTE) {
            throw new DomainException("Solo se puede confirmar un pedido pendiente");
        }
        this.estado = Edo.CONFIRMADO;
    }

    
    public void registrarPago(Pago pago) {
        if (estado != Edo.CONFIRMADO) {
            throw new DomainException("El pedido debe estar confirmado para registrar el pago");
        }

        if (pago == null || !pago.estaAprobado()) {
            throw new DomainException("El pago no está aprobado");
        }

        this.pago = pago;
    }

    public void registrarEnvio(Envio envio) {
        if (pago == null) {
            throw new DomainException("No se puede enviar un pedido sin pago");
        }

        if (envio == null) {
            throw new DomainException("El envío es obligatorio");
        }

        this.envio = envio;
    }

    
    public void cancelar(String motivo) {
        if (estado == Edo.CANCELADO) {
            throw new DomainException("El pedido ya está cancelado");
        }

        if (pago != null) {
            throw new DomainException("No se puede cancelar un pedido con pago registrado");
        }

        if (motivo == null || motivo.length() < 10) {
            throw new DomainException("El motivo de cancelación es inválido");
        }

        this.estado = Edo.CANCELADO;
    }

    private float calcularTotal() {
        float suma = 0;
        for (DetallePedido detalle : detalles) {
            suma += detalle.subtotal().getmonto();
        }
        return suma;
    }

    private String generarFolio() {
        return "PED-" + System.currentTimeMillis();
    }

  
    public UUID getId() {
        return id;
    }

    public String getFolio() {
        return folio;
    }

    public Edo getEstado() {
        return estado;
    }

    public float getTotal() {
        return total;
    }

    public List<DetallePedido> getDetalles() {
        return List.copyOf(detalles);
    }
}
