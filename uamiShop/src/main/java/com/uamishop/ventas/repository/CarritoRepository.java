package com.uamishop.ventas.repository;

import com.uamishop.ventas.domain.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CarritoRepository extends JpaRepository<Carrito, UUID> {
    Optional<Carrito> findByClienteIdAndEstado(UUID clienteId, String estado);
}
