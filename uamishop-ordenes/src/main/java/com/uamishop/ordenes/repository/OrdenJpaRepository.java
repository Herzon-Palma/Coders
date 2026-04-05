package com.uamishop.ordenes.repository;

import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.domain.OrdenId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.uamishop.shared.domain.ClienteId;

public interface OrdenJpaRepository extends JpaRepository<Orden, OrdenId> {
    List<Orden> findByClienteId(ClienteId clienteId);
}
