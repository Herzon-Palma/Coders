package com.uamishop.ordenes.repository;

import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.domain.OrdenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenJpaRepository extends JpaRepository<Orden, OrdenId> {
}
