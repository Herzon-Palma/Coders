package com.uamishop.catalogo.repository;

import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CategoriaRepository extends JpaRepository<Categoria, Categoriaid> {

}
