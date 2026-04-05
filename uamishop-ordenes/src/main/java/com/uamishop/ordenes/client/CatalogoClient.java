package com.uamishop.ordenes.client;

import com.uamishop.ordenes.client.dto.ProductoResumen;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Component
public class CatalogoClient {
    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogoClient(RestTemplate restTemplate, @Value("${catalogo.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "catalogoService", fallbackMethod = "buscarProductoFallback")
    public Optional<ProductoResumen> buscarProducto(UUID productoId) {
        ProductoResumen producto = restTemplate.getForObject(
            baseUrl + "/api/v1/productos/" + productoId,
            ProductoResumen.class
        );
        return Optional.ofNullable(producto);
    }

    /**
     * Fallback cuando el circuit breaker está abierto o Catálogo no responde.
     * Debe tener la misma firma + Throwable al final.
     */
    private Optional<ProductoResumen> buscarProductoFallback(UUID productoId, Throwable t) {
        log.warn("CircuitBreaker [catalogoService] activado para buscarProducto({}): {}",
                productoId, t.getMessage());
        return Optional.empty();
    }
}
