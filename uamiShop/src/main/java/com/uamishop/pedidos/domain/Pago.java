package com.uamishop.pedidos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uamishop.shared.domain.DomainException;

public class Pago {

    private final UUID id;
    private final float monto;
    private final LocalDateTime fechaPago;
    private final String metodo;
    private final String referenciaExterna;
    private EdoPago estado;

   
    public Pago(float monto, String metodo, String referenciaExterna) {
        if (monto <= 0) {
            throw new DomainException("El monto del pago debe ser mayor a cero");
        }
        if (metodo == null || metodo.isBlank()) {
            throw new DomainException("El método de pago es obligatorio");
        }
        if (referenciaExterna == null || referenciaExterna.isBlank()) {
            throw new DomainException("La referencia externa es obligatoria");
        }

        this.id = UUID.randomUUID();
        this.monto = monto;
        this.metodo = metodo;
        this.referenciaExterna = referenciaExterna;
        this.fechaPago = LocalDateTime.now();
        this.estado = EdoPago.PENDIENTE;
    }


    public void aprobar() {
        if (estado != EdoPago.PENDIENTE) {
            throw new DomainException("Solo se puede aprobar un pago pendiente");
        }
        this.estado = EdoPago.APROBADO;
    }

    public void rechazar() {
        if (estado != EdoPago.PENDIENTE) {
            throw new DomainException("Solo se puede rechazar un pago pendiente");
        }
        this.estado = EdoPago.RECHAZADO;
    }

    public void reembolsar() {
        if (estado != EdoPago.APROBADO) {
            throw new DomainException("Solo se puede reembolsar un pago aprobado");
        }
        this.estado = EdoPago.REEMBOLSADO;
    }

    
    public boolean estaAprobado() {
        return estado == EdoPago.APROBADO;
    }

    public float getMonto() {
        return monto;
    }

    public EdoPago getEstado() {
        return estado;
    }
}