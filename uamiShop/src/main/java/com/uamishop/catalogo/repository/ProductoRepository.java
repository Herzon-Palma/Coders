package com.uamishop.catalogo.repository;

import com.uamishop.catalogo.domain.Producto;
import com.uamishop.shared.domain.Productoid;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Productoid> {
    Optional<Producto> findById(Productoid id); //Utilizamos Optional para manejar el posible nulo
}
