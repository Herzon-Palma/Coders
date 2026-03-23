package com.uamishop.ordenes.infraestructura.rest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.api.VentasApi;

@Component
public class VentasApiHttpClient implements VentasApi {

    private final RestTemplate restTemplate;
    private final String ventasBaseUrl;

    public VentasApiHttpClient(RestTemplate restTemplate, @Value("${ventas.service.url}") String ventasBaseUrl) {
        this.restTemplate = restTemplate;
        this.ventasBaseUrl = ventasBaseUrl;
    }

    @Override
    public CarritoResumen obtenerCarrito(UUID carritoId) {
        String url = ventasBaseUrl + "/api/v1/carritos/" + carritoId;
        ResponseEntity<CarritoResumen> response = restTemplate.getForEntity(url, CarritoResumen.class);
        return response.getBody();
    }

    @Override
    public void completarCheckout(UUID carritoId) {
        String url = ventasBaseUrl + "/api/v1/carritos/" + carritoId + "/completar-checkout";
        restTemplate.postForEntity(url, null, Void.class);
    }

    @Override
    public Optional<CarritoResumen> obtenerCarritoActivoDeCliente(UUID clienteId) {
        String url = ventasBaseUrl + "/api/v1/carritos/cliente/" + clienteId + "/activo";
        try {
            ResponseEntity<CarritoResumen> response = restTemplate.getForEntity(url, CarritoResumen.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (Exception e) {
            // Carrito no encontrado
        }
        return Optional.empty();
    }

    @Override
    public void abandonarCarrito(UUID carritoId) {
        String url = ventasBaseUrl + "/api/v1/carritos/" + carritoId + "/abandonar";
        restTemplate.postForEntity(url, null, Void.class);
    }

    @Override
    public Optional<CarritoResumen> obtenerCarritoParaCheckout(UUID clienteUuid) {
        String url = ventasBaseUrl + "/api/v1/carritos/cliente/" + clienteUuid + "/checkout";
        try {
            ResponseEntity<CarritoResumen> response = restTemplate.getForEntity(url, CarritoResumen.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (Exception e) {
            // Carrito no encontrado
        }
        return Optional.empty();
    }
}
