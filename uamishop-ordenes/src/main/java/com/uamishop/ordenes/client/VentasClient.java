package com.uamishop.ordenes.client;

import com.uamishop.ordenes.client.dto.CarritoResumen;
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

    public Optional<CarritoResumen> obtenerCarritoParaCheckout(UUID clienteUuid) {
        try {
            CarritoResumen carrito = restTemplate.getForObject(
                baseUrl + "/api/v1/carritos/checkout/cliente/" + clienteUuid,
                CarritoResumen.class
            );
            return Optional.ofNullable(carrito);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
