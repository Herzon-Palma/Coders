package com.uamishop.ventas.client;

import com.uamishop.ventas.client.dto.ProductoResumen;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import com.uamishop.shared.domain.exception.ServiceUnavailableException;

@Component
public class CatalogoClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogoClient(RestTemplate restTemplate, @Value("${catalogo.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "catalogoService", fallbackMethod = "fallbackBuscarProducto")
    public Optional<ProductoResumen> buscarProducto(UUID productoId) {
        try {
            ProductoResumen producto = restTemplate.getForObject(
                baseUrl + "/api/v1/productos/" + productoId,
                ProductoResumen.class
            );
            return Optional.ofNullable(producto);
        } catch (Exception e) {
            System.err.println("Error calling catalogo: " + e.getMessage());
            throw e; // Lanza para que el Circuit Breaker actúe
        }
    }

    public Optional<ProductoResumen> fallbackBuscarProducto(UUID productoId, Throwable t) {
        System.err.println("Fallback called for buscarProducto: " + t.getMessage());
        throw new ServiceUnavailableException("Servicio de catálogo no disponible temporalmente");
    }
}
