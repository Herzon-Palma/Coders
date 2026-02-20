package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ordenes.domain.exception.OrdenException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {
    @EmbeddedId
    private OrdenId id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cliente_id"))
    })
    private ClienteId clienteId;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @Embedded
    private DireccionEnvio direccionEnvio;

    @Embedded
    private ResumenPago pago;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "orden_id")
    private List<ItemOrden> items = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "orden_historial", joinColumns = @JoinColumn(name = "orden_id"))
    private List<CambioEstado> historial = new ArrayList<>();

    protected Orden() {}

    //Crear una orden nueva
    public Orden(ClienteId clienteId, DireccionEnvio direccion, List<ItemOrden> items) {
        this.id = new OrdenId();
        this.clienteId = clienteId;
        this.direccionEnvio = direccion;
        this.items.addAll(items);
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoOrden.CREADA;
        registrarCambio(null, EstadoOrden.CREADA, "Orden creada");
    }

    //Reglas de Negocio
    public void pagar(String referencia, Money monto) {
        if (this.estado != EstadoOrden.CREADA) throw new OrdenException("Orden ya procesada o cancelada");
        //Validar que el monto coincida con el total
        this.pago = new ResumenPago(referencia, monto);
        cambiarEstado(EstadoOrden.PAGADA, "Pago recibido: " + referencia);
    }

    public void marcarEnviada() {
        if (this.estado != EstadoOrden.PAGADA) throw new OrdenException("La orden debe estar PAGADA para enviarse");
        cambiarEstado(EstadoOrden.EN_TRANSITO, "Orden recolectada por paquetería");
    }

    public void marcarEntregada() {
        if (this.estado != EstadoOrden.EN_TRANSITO) throw new OrdenException("La orden debe estar EN_TRANSITO");
        cambiarEstado(EstadoOrden.ENTREGADA, "Entregada al cliente");
    }

    public void cancelar(String motivo) {
        if (this.estado == EstadoOrden.EN_TRANSITO || this.estado == EstadoOrden.ENTREGADA) {
            throw new OrdenException("No se puede cancelar una orden que ya salió");
        }
        cambiarEstado(EstadoOrden.CANCELADA, motivo);
    }

    private void cambiarEstado(EstadoOrden nuevo, String motivo) {
        registrarCambio(this.estado, nuevo, motivo);
        this.estado = nuevo;
    }

    private void registrarCambio(EstadoOrden anterior, EstadoOrden nuevo, String motivo) {
        this.historial.add(new CambioEstado(anterior, nuevo, motivo));
    }
    
    public OrdenId getId() { return id; }
    public EstadoOrden getEstado() { return estado; }
}
