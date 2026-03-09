package com.uamishop.ordenes.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.shared.event.*;
import com.uamishop.ordenes.domain.*;
import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import com.uamishop.shared.event.ProductoCompradoEvent;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.api.VentasApi;
import com.uamishop.shared.domain.exception.DomainException;
import com.uamishop.ordenes.repository.OrdenJpaRepository;
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.uamishop.ordenes.api.OrdenesApi;
import com.uamishop.ordenes.controller.OrdenResponse;
import com.uamishop.ordenes.api.OrdenResumen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Transactional
public class OrdenService implements OrdenesApi {

    private final OrdenJpaRepository repository;
    private final CatalogoApi catalogoApi;
    private final VentasApi ventasApi;

    private final ApplicationEventPublisher eventPublisher;

    public OrdenService(OrdenJpaRepository repository, CatalogoApi catalogoApi, VentasApi ventasApi,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.catalogoApi = catalogoApi;
        this.ventasApi = ventasApi;
        this.eventPublisher = eventPublisher;
    }

    public Orden crearOrden(UUID clienteUuid, DireccionEnvio direccion, List<ItemDto> itemsDto) {
        // Convertir DTOs a Entidades de Dominio, validando contra el catálogo
        List<ItemOrden> items = itemsDto.stream().map(d -> {
            // Buscar el producto en el catálogo
            ProductoResumen producto = catalogoApi.buscarProducto(d.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El producto no existe en el catálogo: " + d.productoId()));

            if (!producto.disponible()) {
                throw new DomainException(
                        "El producto no está disponible para la venta: " + producto.nombre());
            }

            ProductoRef ref = new ProductoRef(new Productoid(d.productoId()), producto.nombre(), producto.sku());
            Money precio = new Money(producto.precio(), producto.moneda());
            return new ItemOrden(ref, BigDecimal.valueOf(d.cantidad()), precio);
        })
                .collect(Collectors.toList());

        // Crear ResumenPago inicial (Pendiente)
        ResumenPago pagoInicial = ResumenPago.crear("PENDIENTE;1970-01-01T00:00:00;0.00 MXN"); // Provisorio para no
                                                                                               // fallar
        // regex

        Orden orden = repository.save(Orden.crear(new ClienteId(clienteUuid), items, direccion, pagoInicial));

        // Publicar evento de productos comprados
        ProductoCompradoEvent event = new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                orden.getId().id(),
                clienteUuid,
                items.stream().map(item -> new ProductoCompradoEvent.ProductoComprado(
                        item.getProductoRef().productoid().getValue(),
                        item.getProductoRef().sku(),
                        item.getCantidad().intValue(),
                        item.getPrecioUnitario().cantidad(),
                        item.getPrecioUnitario().moneda())).collect(Collectors.toList()));

        eventPublisher.publishEvent(event);

        return orden;
    }

    public OrdenResponse crearDesdeCarrito(UUID clienteUuid, DireccionEnvio direccion) {
        // Buscar el carrito en CHECKOUT del cliente vía API de Ventas
        CarritoResumen carrito = ventasApi.obtenerCarritoParaCheckout(clienteUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un carrito en CHECKOUT para el cliente: " + clienteUuid));

        // Convertir los items del carrito a ItemDto (productoId es Productoid,
        // extraemos el UUID)
        List<ItemDto> itemsDto = carrito.items().stream()
                .map(i -> new ItemDto(i.productoId().getValue(), i.cantidad()))
                .collect(Collectors.toList());

        // Crear la orden (valida catálogo, guarda y publica evento
        // ProductoCompradoEvent)
        Orden ordenCreada = crearOrden(clienteUuid, direccion, itemsDto);

        // Publicar evento OrdenCreadaEvent para que Ventas complete el checkout de
        // forma asíncrona
        eventPublisher.publishEvent(new OrdenCreadaEvent(
                UUID.randomUUID(),
                Instant.now(),
                ordenCreada.getId().id(),
                carrito.carritoId(),
                clienteUuid));

        return new OrdenResponse(
                ordenCreada.getId().id(),
                ordenCreada.getEstado().name(),
                ordenCreada.getClienteId().getId(),
                itemsDto);
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

    // DTO simple — nombre y precio se obtienen del catálogo
    public record ItemDto(
            @NotNull(message = "El productoId no puede ser nulo") UUID productoId,
            @Positive(message = "La cantidad debe ser mayor a cero") int cantidad) {
    }
}
