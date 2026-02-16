package com.uamishop.ventas.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.*;
import com.uamishop.ventas.domain.exception.CarritoException;
import com.uamishop.ventas.repository.CarritoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class CarritoService {

    private final CarritoJpaRepository repository;

    public CarritoService(CarritoJpaRepository repository) {
        this.repository = repository;
    }

    public Carrito crear(ClienteId clienteId) {
        Carrito carrito = new Carrito(clienteId); // Estado ACTIVO por defecto 
        return repository.save(carrito);
    }

    public Carrito obtenerCarrito(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarritoException("Carrito no encontrado")); [cite: 166]
    }

    public Carrito agregarProducto(UUID id, ProductoRef producto, int cantidad, Money precio) {
        Carrito carrito = obtenerCarrito(id);
        carrito.agregarProducto(producto, cantidad, precio); // Reglas RN-VEN-01 a 04 
        return repository.save(carrito);
    }

    public Carrito modificarCantidad(UUID id, UUID productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(id);
        carrito.modificarCantidad(productoId, nuevaCantidad); // Regla RN-VEN-05 y 06 
        return repository.save(carrito);
    }

    public Carrito eliminarProducto(UUID id, UUID productoId) {
        Carrito carrito = obtenerCarrito(id);
        carrito.eliminarProducto(productoId); // Regla RN-VEN-07 y 08 
        return repository.save(carrito);
    }

    public void vaciar(UUID id) {
        Carrito carrito = obtenerCarrito(id);
        carrito.vaciar(); // Regla RN-VEN-09 
        repository.save(carrito);
    }

    public Carrito iniciarCheckout(UUID id) {
        Carrito carrito = obtenerCarrito(id);
        carrito.iniciarCheckout(); // Reglas RN-VEN-10 a 12 
        return repository.save(carrito);
    }
}
