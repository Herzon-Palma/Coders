package com.uamishop.ventas.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.api.ItemCarritoResumen;
import com.uamishop.ventas.api.VentasApi;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.repository.CarritoRepository;
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.shared.domain.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoService implements VentasApi {

    private final CarritoRepository carritoRepository;
    private final CatalogoApi catalogoApi;

    public CarritoService(CarritoRepository carritoRepository, CatalogoApi catalogoApi) {
        this.carritoRepository = carritoRepository;
        this.catalogoApi = catalogoApi;
    }

    @Transactional
    public Carrito crear(ClienteId clienteId) {
        // Podríamos validar si ya existe un carrito ACTIVO para este cliente antes de
        // crear otro
        Carrito nuevoCarrito = Carrito.crear(clienteId);
        return carritoRepository.save(nuevoCarrito);
    }

    @Transactional(readOnly = true)
    public Carrito obtenerCarrito(CarritoId carritoId) {
        return carritoRepository.findById(carritoId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + carritoId.id()));
    }

    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, Productoid productoId, int cantidad) {
        Carrito carrito = obtenerCarrito(carritoId);

        // Buscar el producto en el catálogo (Integración entre módulos via API)
        ProductoResumen producto = catalogoApi.buscarProducto(productoId.getValue())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto no existe en el catálogo: " + productoId.getValue()));

        if (!producto.disponible()) {
            throw new DomainException("El producto no está disponible para la venta: " + producto.nombre());
        }

        ProductoRef ref = new ProductoRef(productoId, producto.nombre(), producto.sku());
        Money precio = new Money(producto.precio(), producto.moneda());

        carrito.agregarProducto(ref, cantidad, precio);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, ProductoRef productoRef, int cantidad, Money precioUnitario) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.agregarProducto(productoRef, cantidad, precioUnitario);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito modificarCantidad(CarritoId carritoId, Productoid productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-05 y 06 validados en el dominio
        carrito.modificarCantidad(productoId, nuevaCantidad);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito eliminarProducto(CarritoId carritoId, Productoid productoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-07 y 08 validados en el dominio
        carrito.eliminarProducto(productoId);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito vaciar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-09 validado en el dominio
        carrito.vaciar();

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito iniciarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-10, 11 y 12 validados en el dominio
        carrito.iniciarCheckout();

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito completarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-13 validado en el dominio
        carrito.completarCheckout();

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito abandonar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        // RN-VEN-14 validado en el dominio
        carrito.abandonar();

        return carritoRepository.save(carrito);
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoResumen obtenerCarrito(UUID carritoId) {
        Carrito carrito = carritoRepository.findById(new CarritoId(carritoId))
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + carritoId));
        return mapToResumen(carrito);
    }

    @Override
    @Transactional
    public void completarCheckout(UUID carritoId) {
        completarCheckout(new CarritoId(carritoId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarritoResumen> obtenerCarritoActivoDeCliente(UUID clienteId) {
        return carritoRepository
                .findByClienteIdAndEstado(new ClienteId(clienteId), com.uamishop.ventas.domain.EstadoCarrito.ACTIVO)
                .map(this::mapToResumen);
    }

    @Override
    @Transactional
    public void abandonarCarrito(UUID carritoId) {
        abandonar(new CarritoId(carritoId));
    }

    private CarritoResumen mapToResumen(Carrito carrito) {
        List<ItemCarritoResumen> items = carrito.getItems().stream()
                .map(item -> new ItemCarritoResumen(
                        item.getProductoRef().productoid(),
                        item.getProductoRef().nombreProducto(),
                        item.getProductoRef().sku(),
                        item.getCantidad().intValue(),
                        item.getPrecioUnitario()))
                .collect(Collectors.toList());

        return new CarritoResumen(
                carrito.getId().id(),
                carrito.getClienteId(),
                carrito.getEstado().name(),
                items);
    }
}
