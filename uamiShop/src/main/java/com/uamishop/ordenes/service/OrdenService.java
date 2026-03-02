package com.uamishop.ordenes.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.ordenes.domain.*;
import com.uamishop.ordenes.domain.OrdenException;
import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.uamishop.ordenes.api.OrdenesApi;
import com.uamishop.ordenes.api.OrdenResumen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Transactional
public class OrdenService implements OrdenesApi {

    private final OrdenJpaRepository repository;

    public OrdenService(OrdenJpaRepository repository) {
        this.repository = repository;
    }

    public Orden crearOrden(UUID clienteUuid, DireccionEnvio direccion, List<ItemDto> itemsDto) {
        // Convertir DTOs a Entidades de Dominio
        List<ItemOrden> items = itemsDto.stream().map(d -> {
            // Generamos un SKU temporal válido (AAA-000)
            String sku = "TMP-123";
            ProductoRef ref = new ProductoRef(new Productoid(d.productoId()), d.nombre(), sku);
            return new ItemOrden(ref, BigDecimal.valueOf(d.cantidad()), Money.pesos(d.precio()));
        })
                .collect(Collectors.toList());

        // Crear ResumenPago inicial (Pendiente)
        ResumenPago pagoInicial = ResumenPago.crear("PENDIENTE;1970-01-01T00:00:00;0.00 MXN"); // Provisorio para no
                                                                                               // fallar
        // regex

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
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
    }

    @Override
    public Optional<OrdenResumen> obtenerOrden(UUID ordenId) {
        return repository.findById(new OrdenId(ordenId)).map(this::mapToResumen);
    }

    @Override
    public List<OrdenResumen> obtenerOrdenesPorCliente(UUID clienteId) {
        // Obtenemos todas y filtramos por clienteId
        return repository.findAll().stream()
                .filter(o -> o.getClienteId().getId().equals(clienteId))
                .map(this::mapToResumen)
                .collect(Collectors.toList());
    }

    private OrdenResumen mapToResumen(Orden orden) {
        List<OrdenResumen.ItemOrdenResumen> itemsResumen = orden.getItems().stream()
                .map(item -> new OrdenResumen.ItemOrdenResumen(
                        item.getProductoRef().productoid(),
                        item.getProductoRef().sku(),
                        item.getProductoRef().nombreProducto(),
                        item.getCantidad().intValue(),
                        item.getPrecioUnitario()))
                .collect(Collectors.toList());

        return new OrdenResumen(
                orden.getId().id(), // sacamos el UUID del record interno
                orden.getClienteId(), // El VO ya es SharedKernel
                orden.getEstado().name(),
                orden.getSubtotal(),
                orden.getTotal(),
                itemsResumen);
    }

    // DTO simple
    public record ItemDto(
            @NotNull(message = "El productoId no puede ser nulo") UUID productoId,
            @NotBlank(message = "El nombre del producto es obligatorio") String nombre,
            @Positive(message = "La cantidad debe ser mayor a cero") int cantidad,
            @Positive(message = "El precio debe ser mayor a cero") double precio) {
    }
}
