package com.uamishop.ventas.repository;


import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.EstadoCarrito;
import com.uamishop.shared.domain.ClienteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, CarritoId> {
    Optional<Carrito> findByClienteIdAndEstado(ClienteId clienteId, EstadoCarrito estado);

    Optional<Carrito> findById(CarritoId carritoId);
}


