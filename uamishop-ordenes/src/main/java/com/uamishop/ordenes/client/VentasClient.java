package com.uamishop.ordenes.client;

import com.uamishop.ordenes.client.dto.CarritoResumen;
import com.uamishop.shared.domain.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Component
public class VentasClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public VentasClient(RestTemplate restTemplate, @Value("${ventas.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "ventasService", fallbackMethod = "fallbackObtenerCarrito")
    public Optional<CarritoResumen> obtenerCarritoParaCheckout(UUID clienteUuid) {
        try {
            CarritoResumen carrito = restTemplate.getForObject(
                baseUrl + "/api/v1/carritos/checkout/cliente/" + clienteUuid,
                CarritoResumen.class
            );
            return Optional.ofNullable(carrito);
        } catch (Exception e) {
            System.err.println("Error calling ventas: " + e.getMessage());
            throw e;
        }
    }

    public Optional<CarritoResumen> fallbackObtenerCarrito(UUID clienteUuid, Throwable t) {
        System.err.println("[ordenes] Fallback ventas: " + t.getMessage());
        throw new ServiceUnavailableException("Servicio de ventas no disponible temporalmente");
    }
}
