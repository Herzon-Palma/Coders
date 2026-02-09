package com.uamishop.pedidos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uamishop.shared.domain.DomainException;

public class Envio {

    private final UUID id;
    private final String direccionDestino;
    private final LocalDateTime fechaEnvio;
    private final String numeroGuia;
    private EdoEnvio estado;

   
    public Envio(String direccionDestino, String numeroGuia) {
        if (direccionDestino == null || direccionDestino.isBlank()) {
            throw new DomainException("La dirección de destino es obligatoria");
        }
        if (numeroGuia == null || numeroGuia.length() < 10) {
            throw new DomainException("El número de guía es inválido");
        }

        this.id = UUID.randomUUID();
        this.direccionDestino = direccionDestino;
        this.numeroGuia = numeroGuia;
        this.fechaEnvio = LocalDateTime.now();
        this.estado = EdoEnvio.EN_PREPARACION;
    }

   
    public void marcarEnTransito() {
        if (estado != EdoEnvio.EN_PREPARACION) {
            throw new DomainException("Solo se puede pasar a tránsito desde preparación");
        }
        this.estado = EdoEnvio.EN_TRANSITO;
    }

    public void marcarEntregado() {
        if (estado != EdoEnvio.EN_TRANSITO) {
            throw new DomainException("Solo se puede entregar un envío en tránsito");
        }
        this.estado = EdoEnvio.ENTREGADO;
    }

    public EdoEnvio getEstado() {
        return estado;
    }
}