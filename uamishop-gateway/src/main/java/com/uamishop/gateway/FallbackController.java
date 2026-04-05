package com.uamishop.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controller de fallback para el Circuit Breaker del API Gateway.
 * Cuando un microservicio no responde (circuito abierto),
 * Spring Cloud Gateway redirige aquí en lugar de dejar al cliente esperando.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/catalogo")
    public ResponseEntity<Map<String, Object>> fallbackCatalogo() {
        return buildFallback("uamishop-catalogo");
    }

    @RequestMapping("/ordenes")
    public ResponseEntity<Map<String, Object>> fallbackOrdenes() {
        return buildFallback("uamishop-ordenes");
    }

    @RequestMapping("/ventas")
    public ResponseEntity<Map<String, Object>> fallbackVentas() {
        return buildFallback("uamishop-ventas");
    }

    private ResponseEntity<Map<String, Object>> buildFallback(String servicio) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "Servicio no disponible",
                "servicio", servicio,
                "mensaje", "El servicio " + servicio
                    + " no está respondiendo. Intente de nuevo más tarde.",
                "timestamp", Instant.now().toString()
            ));
    }
}
