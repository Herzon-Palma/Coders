package com.uamishop.catalogo.repository;

import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.domain.Productoid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Productoid> {

}
