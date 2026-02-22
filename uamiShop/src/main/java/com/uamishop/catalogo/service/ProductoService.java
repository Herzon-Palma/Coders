package com.uamishop.catalogo.service;

import com.uamishop.catalogo.domain.*;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.controller.dto.*;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;

import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        Categoriaid categoriaId = new Categoriaid(request.categoriaid());
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("La categoría especificada no existe");
        }

        Producto producto = Producto.crear(
                request.nombre(),
                request.descripcion(),
                new Money(request.precio(), request.moneda()), // Asumimos USD, esto podría ser parte del request
                categoriaId);
        return productoRepository.save(producto).toResponse();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(UUID id) {
        Productoid productoid = new Productoid(id);
        // Utiolizamos Optional para manejar el posible nulo
        Optional<Producto> productoOpt = productoRepository.findById(productoid);
        if (productoOpt.isEmpty()) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        return productoOpt.get().toResponse();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarTodos() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(Producto::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponse actualizar(UUID id, ProductoRequest request) {
        Productoid productoid = new Productoid(id);
        Producto producto = productoRepository.findById(productoid)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.actualizar(request.nombre(), request.descripcion(), new Money(request.precio(), request.moneda()));
        return productoRepository.save(producto).toResponse();
    }

    @Transactional
    public void activar(UUID id) {
        Productoid productoid = new Productoid(id);

        Producto producto = productoRepository.findById(productoid)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.activar();
        productoRepository.save(producto);
    }

    @Transactional
    public void desactivar(UUID id) {
        Productoid productoid = new Productoid(id);
        Producto producto = productoRepository.findById(productoid)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.desactivar();
        productoRepository.save(producto);
    }

}
