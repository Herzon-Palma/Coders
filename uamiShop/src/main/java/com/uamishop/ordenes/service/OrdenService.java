package com.uamishop.ordenes.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ordenes.domain.*;
import com.uamishop.ordenes.domain.OrdenException;
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
        //Convertir DTOs a Entidades de Dominio
        List<ItemOrden> items = itemsDto.stream()
            .map(d -> new ItemOrden(d.productoId(), d.nombre(), BigDecimal.valueOf(d.cantidad()) , Money.pesos(d.precio())))
            .collect(Collectors.toList());

        Orden orden = new Orden(new ClienteId(clienteUuid), direccion, items);
        return repository.save(orden);
    }

    public Orden pagarOrden(UUID ordenId, String referencia) {
        Orden orden = buscar(ordenId);
        //Calcular el total
        orden.pagar(referencia, Money.pesos(0)); 
        return repository.save(orden);
    }

    public Orden enviarOrden(UUID ordenId) {
        Orden orden = buscar(ordenId);
        orden.marcarEnviada();
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
                .orElseThrow(() -> new OrdenException("Orden no encontrada"));
    }
    
    public record ItemDto(UUID productoId, String nombre, int cantidad, double precio) {}
}
