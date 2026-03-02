package com.uamishop.catalogo.service;

import com.uamishop.catalogo.domain.*;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
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
public class ProductoService implements CatalogoApi {
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
                request.sku(),
                new Money(request.precio(), request.moneda()),
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


    //Aquí inicia la implementación de nuesta API de catalogo

    @Override
    public Optional<ProductoResumen> buscarProducto(UUID productoId) {
        return productoRepository.findById(new Productoid(productoId))
                .map(this::toResumen); 
                /*  el .map funciona como un "ifPresent" que transforma el Producto 
                   a ProductoResumen si existe, o devuelve Optional.empty() si no existe
                */
    }
    
    @Override
    public List<ProductoResumen> buscarProductos(List<UUID> productoIds) {
        List<Productoid> ids = productoIds.stream()
                .map(Productoid::new)
                .collect(Collectors.toList());
                // el collect simplemente es para convertir el Stream 
                // de Productoid a una List<Productoid>
        return productoRepository.findAllById(ids).stream()
                .map(this::toResumen)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeProducto(UUID productoId) {
        return productoRepository.existsById(new Productoid(productoId));
    }

    @Override
    public boolean estaDisponible(UUID productoId) {
        return productoRepository.findById(new Productoid(productoId))
                .map(Producto::isDisponible)
                .orElse(false);
    }

    @Override
    public Optional<Money> obtenerPrecio(UUID productoId) {
        return productoRepository.findById(new Productoid(productoId))
                .map(Producto::getPrecio);
    }

    @Override
    public boolean existeCategoria(UUID categoriaId) {
        return categoriaRepository.existsById(new Categoriaid(categoriaId));}

    //Utilizamos este mapper para convertir de Producto a ProductoResumen, que es lo que exponemos en la API pública
    //para que otros dominios puedan consultar sin exponer toda la información interna del producto.
    private ProductoResumen toResumen(Producto producto) {
        String categoriaNombre = categoriaRepository
                .findById(producto.getCategoriaId())
                .map(Categoria::getNombre)
                .orElse(null);

        return new ProductoResumen(
                producto.getId().valor(),
                producto.getNombre(),
                producto.getSku(),
                producto.getPrecio().cantidad(),
                producto.getPrecio().moneda(),
                producto.getCategoriaId().valor(),
                categoriaNombre,
                producto.isDisponible()
        );
    }

    
}
