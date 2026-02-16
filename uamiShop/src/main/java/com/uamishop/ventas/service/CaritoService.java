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
    private final CarritoRepository repository;

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    public Carrito obtenerOCrearCarrito(UUID clienteId) {
        return repository.findByClienteIdAndEstado(clienteId, "ACTIVO")
                .orElseGet(() -> repository.save(new Carrito(clienteId)));
    }

    public Carrito agregarProducto(UUID carritoId, ProductoDTO dto) {
        Carrito carrito = repository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        carrito.agregarProducto(dto.productoId(), dto.nombre(), dto.cantidad(), dto.precio());
        return repository.save(carrito);
    }

    public void eliminarProducto(UUID carritoId, UUID productoId) {
        Carrito carrito = repository.findById(carritoId).orElseThrow();
        carrito.getItems().removeIf(item -> item.getProductoId().equals(productoId));
        repository.save(carrito);
    }

    public Carrito finalizarCompra(UUID carritoId) {
        Carrito carrito = repository.findById(carritoId).orElseThrow();
        carrito.iniciarCheckout();
        return repository.save(carrito);
    }
}
