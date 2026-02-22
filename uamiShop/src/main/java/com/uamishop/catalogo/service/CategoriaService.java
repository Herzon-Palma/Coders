package com.uamishop.catalogo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.springframework.stereotype.Service;

import com.uamishop.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.catalogo.controller.dto.CategoriaResponse;
import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.domain.Categoriaid;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.shared.domain.exception.DomainException;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
   }
    
    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        Categoria categoria = Categoria.crear(Categoriaid.generar(), request.nombre(), request.descripcion());
        return categoriaRepository.save(categoria).toResponse();
    }

    @Transactional
    public List<CategoriaResponse> buscarTodasCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        
        List<CategoriaResponse> resultado = new ArrayList<>();
        for (Categoria c : categorias) {
            resultado.add(c.toResponse());
        }
        return resultado;
    }

    @Transactional
    public CategoriaResponse actualizarCategoria(UUID id, CategoriaRequest request) {
        Categoriaid categoriaId = new Categoriaid(id);
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new DomainException("Categoría no encontrada"));
    
        categoria.actualizar(request.nombre(), request.descripcion());
        return categoriaRepository.save(categoria).toResponse();
    }
}
