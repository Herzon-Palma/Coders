package com.uamishop.ventas.repository;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, CarritoId> {
    
    Optional<Carrito> findByClienteIdAndEstado(ClienteId clienteId, EstadoCarrito estado);
}
