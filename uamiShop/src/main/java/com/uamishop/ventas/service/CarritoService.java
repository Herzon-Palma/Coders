package com.uamishop.ventas.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
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
    public Carrito agregarProducto(CarritoId carritoId, ProductoRef productoRef, int cantidad, Money precioUnitario) {
        Carrito carrito = obtenerCarrito(carritoId);

        // La lógica de RN-VEN-01 a 04 ocurre dentro del Agregado
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
}
