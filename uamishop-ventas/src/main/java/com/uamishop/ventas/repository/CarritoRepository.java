package com.uamishop.ventas.repository;


import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.EstadoCarrito;
import com.uamishop.shared.domain.ClienteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, CarritoId> {
    // Nota: por reglas de negocio/uso del frontend puede haber mas de un carrito por cliente.
    // Para evitar NonUniqueResultException al buscar por (cliente, estado), preferimos el mas reciente.
    Optional<Carrito> findFirstByClienteIdAndEstadoOrderByFechaActualizacionDesc(ClienteId clienteId, EstadoCarrito estado);

    List<Carrito> findAllByClienteIdAndEstado(ClienteId clienteId, EstadoCarrito estado);

    Optional<Carrito> findById(CarritoId carritoId);
}


