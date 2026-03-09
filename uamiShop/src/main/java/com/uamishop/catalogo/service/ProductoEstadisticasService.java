package com.uamishop.catalogo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.uamishop.catalogo.domain.ProductoEstadisticas;
import com.uamishop.catalogo.repository.ProductoEstadisticasJpaRepository;

@Service
public class ProductoEstadisticasService {
    private final ProductoEstadisticasJpaRepository repository;

    public ProductoEstadisticasService(ProductoEstadisticasJpaRepository repository) {
        this.repository = repository;
    }

    public void registrarVenta(UUID productoId, int cantidad) {
        Optional<ProductoEstadisticas> optional = repository.findById(productoId);
        if(optional.isPresent()) {
            ProductoEstadisticas estadisticas = optional.get();
            estadisticas.registrarVenta(cantidad);
            repository.save(estadisticas);
        } else {
            // Si no existe, creamos una nueva entrada con la cantidad vendida
            ProductoEstadisticas nuevaEstadistica = new ProductoEstadisticas(productoId, cantidad);
            repository.save(nuevaEstadistica);
        }
    }

    public void registrarAgregadoCarrito(UUID productoId){
        Optional<ProductoEstadisticas> optional = repository.findById(productoId);
        if(optional.isPresent()) {
            ProductoEstadisticas estadisticas = optional.get();
            estadisticas.registrarAgregadoCarrito(productoId);
            repository.save(estadisticas);
        } else {
            // Si no existe, creamos una nueva entrada con 1 agregado al carrito
            ProductoEstadisticas nuevaEstadistica = new ProductoEstadisticas(productoId, 0);
            nuevaEstadistica.registrarAgregadoCarrito(productoId);
            repository.save(nuevaEstadistica);
        }

    }

    //Metodo para obtener los productos más vendidos por la cantidadVendida
    public List<ProductoEstadisticas> obtenerMasVendidos(int limit) {
        return repository.findMasVendidos(limit);
    }

    public ProductoEstadisticas obtenerEstadisticas(UUID productoId) {
        return repository.findById(productoId).orElse(new ProductoEstadisticas(productoId));
    }
}
