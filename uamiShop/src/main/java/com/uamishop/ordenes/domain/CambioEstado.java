package com.uamishop.ordenes.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class CambioEstado {
    private LocalDateTime fecha;
    private String estadoAnterior;
    private String nuevoEstado;
    private String motivo;

    protected CambioEstado() {}

    public CambioEstado(EstadoOrden anterior, EstadoOrden nuevo, String motivo) {
        this.fecha = LocalDateTime.now();
        this.estadoAnterior = anterior != null ? anterior.name() : "N/A";
        this.nuevoEstado = nuevo.name();
        this.motivo = motivo;
    }
}
