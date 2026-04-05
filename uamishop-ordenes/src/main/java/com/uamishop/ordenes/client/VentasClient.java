package com.uamishop.ordenes.client;

import com.uamishop.ordenes.client.dto.CarritoResumen;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Component
public class VentasClient {
    private static final Logger log = LoggerFactory.getLogger(VentasClient.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public VentasClient(RestTemplate restTemplate, @Value("${ventas.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "ventasService", fallbackMethod = "obtenerCarritoParaCheckoutFallback")
    public Optional<CarritoResumen> obtenerCarritoParaCheckout(UUID clienteUuid) {
        CarritoResumen carrito = restTemplate.getForObject(
            baseUrl + "/api/v1/carritos/checkout/cliente/" + clienteUuid,
            CarritoResumen.class
        );
        return Optional.ofNullable(carrito);
    }

    /**
     * Fallback cuando el circuit breaker está abierto o Ventas no responde.
     */
    private Optional<CarritoResumen> obtenerCarritoParaCheckoutFallback(UUID clienteUuid, Throwable t) {
        log.warn("CircuitBreaker [ventasService] activado para obtenerCarritoParaCheckout({}): {}",
                clienteUuid, t.getMessage());
        return Optional.empty();
    }
}
