package com.uamishop.ventas.service;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository repository;

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    public Carrito obtenerCarritoActivo(UUID clienteId) {
        return repository.findByClienteIdAndEstado(clienteId, "ACTIVO")
                .orElseGet(() -> repository.save(new Carrito(clienteId)));
    }

    public Carrito agregarProducto(UUID carritoId, UUID prodId, String nombre, int cantidad, double precio) {
        Carrito carrito = repository.findById(carritoId)
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));
        
        carrito.agregarProducto(prodId, nombre, cantidad, Money.of(precio));
        
        return repository.save(carrito);
    }

    public void eliminarProducto(UUID carritoId, UUID productoId) {
        Carrito carrito = repository.findById(carritoId)
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));
        
        carrito.eliminarProducto(productoId);
        repository.save(carrito);
    }

    public Carrito checkout(UUID carritoId) {
        Carrito carrito = repository.findById(carritoId)
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));
        
        carrito.iniciarCheckout();
        return repository.save(carrito);
    }
}
