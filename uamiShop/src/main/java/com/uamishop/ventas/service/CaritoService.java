package com.uamishop.ventas.service;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.ventas.domain.*;
import com.uamishop.ventas.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    public Carrito crearOObtenerCarrito(UUID clienteId) {
        return carritoRepository.findByClienteIdAndEstado(clienteId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.crear(clienteId);
                    return carritoRepository.save(nuevo);
                });
    }

    public void agregarProducto(UUID carritoId, UUID productoId, String nombre, BigDecimal precio, int cantidad) {
        Carrito carrito = carritoRepository.findById(new CarritoId(carritoId))
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));

        ProductoRef producto = new ProductoRef(productoId, nombre, "SKU-GENERICO", Money.pesos(precio));

        carrito.agregarProducto(producto, cantidad);
        carritoRepository.save(carrito);
    }

    public void iniciarCheckout(UUID carritoId) {
        Carrito carrito = carritoRepository.findById(new CarritoId(carritoId))
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));
        
        carrito.iniciarCheckout();
        carritoRepository.save(carrito);
    }
    
    public Carrito obtenerPorId(UUID carritoId) {
         return carritoRepository.findById(new CarritoId(carritoId))
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));
    }
}
