package com.uamishop.ordenes.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.ordenes.domain.*;
import com.uamishop.ordenes.domain.OrdenException;
import com.uamishop.ordenes.domain.exception.OrdenDomainException;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenService {

    private final OrdenJpaRepository repository;

    public OrdenService(OrdenJpaRepository repository) {
        this.repository = repository;
    }

    public Orden crearOrden(UUID clienteUuid, DireccionEnvio direccion, List<ItemDto> itemsDto) {
        // Convertir DTOs a Entidades de Dominio
        List<ItemOrden> items = itemsDto.stream().map(d -> {
            // Generamos un SKU temporal si no viene en DTO (debería venir)
            String sku = "SKU-" + d.productoId().toString().substring(0, 3).toUpperCase();
            ProductoRef ref = new ProductoRef(new Productoid(d.productoId()), d.nombre(), sku);
            return new ItemOrden(ref, BigDecimal.valueOf(d.cantidad()), Money.pesos(d.precio()));
        })
                .collect(Collectors.toList());

        // Crear ResumenPago inicial (Pendiente)
        ResumenPago pagoInicial = ResumenPago.crear("TARJETA"); // Default method

        return repository.save(Orden.crear(new ClienteId(clienteUuid), items, direccion, pagoInicial));
    }

    public Orden confirmarOrden(UUID ordenId) {
        Orden orden = buscar(ordenId);
        orden.confirmar();
        return repository.save(orden);
    }

    public Orden pagarOrden(UUID ordenId, String referencia) {
        Orden orden = buscar(ordenId);
        // Calcular el total
        orden.pagar(referencia, Money.pesos(0));
        if (orden.obtenerEstadoActual() == EstadoOrden.PENDIENTE) {
            orden.confirmar();
        }
        orden.procesarPago(referencia);
        return repository.save(orden);
    }

    public Orden enviarOrden(UUID ordenId, InfoEnvio infoEnvio) {
        Orden orden = buscar(ordenId);
        if (orden.obtenerEstadoActual() == EstadoOrden.PAGO_PROCESADO) {
            orden.marcarEnProceso();
        }
        orden.marcarEnviada(infoEnvio);
        return repository.save(orden);
    }

    public Orden entregarOrden(UUID ordenId) {
        Orden orden = buscar(ordenId);
        orden.marcarEntregada();
        return repository.save(orden);
    }

    public Orden cancelarOrden(UUID ordenId, String motivo) {
        Orden orden = buscar(ordenId);
        orden.cancelar(motivo);
        return repository.save(orden);
    }

    public Orden buscar(UUID id) {
        return repository.findById(new OrdenId(id))
                .orElseThrow(() -> new OrdenDomainException("Orden no encontrada"));
    }

    // DTO simple
    public record ItemDto(UUID productoId, String nombre, int cantidad, double precio) {
    }
}
