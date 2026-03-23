package com.uamishop.ordenes.infraestructura.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.shared.domain.Money;

@Component
public class CatalogoApiHttpClient implements CatalogoApi {

    private final RestTemplate restTemplate;
    private final String catalogoBaseUrl;

    public CatalogoApiHttpClient(RestTemplate restTemplate, @Value("${catalogo.service.url}") String catalogoBaseUrl) {
        this.restTemplate = restTemplate;
        this.catalogoBaseUrl = catalogoBaseUrl;
    }

    @Override
    public Optional<ProductoResumen> buscarProducto(UUID productoId) {
        String url = catalogoBaseUrl + "/api/v1/productos/" + productoId;
        ResponseEntity<ProductoResumen> response = restTemplate.getForEntity(url, ProductoResumen.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return Optional.empty();
        }
        return Optional.of(response.getBody());
    }

    @Override
    public List<ProductoResumen> buscarProductos(List<UUID> productoIds) {
        String url = catalogoBaseUrl + "/api/v1/productos/batch";
        ResponseEntity<ProductoResumen[]> response = restTemplate.postForEntity(url, productoIds, ProductoResumen[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return List.of();
        }
        return List.of(response.getBody());
    }

    @Override
    public boolean existeProducto(UUID productoId) {
        String url = catalogoBaseUrl + "/api/v1/productos/" + productoId + "/exists";
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean estaDisponible(UUID productoId) {
        String url = catalogoBaseUrl + "/api/v1/productos/" + productoId + "/available";
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Optional<Money> obtenerPrecio(UUID productoId) {
        String url = catalogoBaseUrl + "/api/v1/productos/" + productoId + "/precio";
        ResponseEntity<Money> response = restTemplate.getForEntity(url, Money.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return Optional.empty();
        }
        return Optional.of(response.getBody());
    }

    @Override
    public boolean existeCategoria(UUID categoriaId) {
        String url = catalogoBaseUrl + "/api/v1/categorias/" + categoriaId + "/exists";
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}
