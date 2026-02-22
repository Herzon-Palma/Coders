package com.uamishop.ordenes.controller;

import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.domain.OrdenId;
import com.uamishop.ordenes.domain.InfoEnvio;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Orden> crear(@RequestBody CrearOrdenRequest request) {
        return ResponseEntity.ok(service.crearOrden(
                request.clienteId(),
                request.direccion(),
                request.items()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Orden> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarOrden(id));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Orden> pagar(@PathVariable UUID id, @RequestBody String referencia) {
        return ResponseEntity.ok(service.pagarOrden(id, referencia));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<Orden> enviar(@PathVariable UUID id, @RequestBody InfoEnvio infoEnvio) {
        return ResponseEntity.ok(service.enviarOrden(id, infoEnvio));
    }

    @PostMapping("/{id}/entregar")
    public ResponseEntity<Orden> entregar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.entregarOrden(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Orden> cancelar(@PathVariable UUID id, @RequestBody String motivo) {
        return ResponseEntity.ok(service.cancelarOrden(id, motivo));
    }
}

// DTOs
record CrearOrdenRequest(
        UUID clienteId,
        DireccionEnvio direccion,
        List<OrdenService.ItemDto> items) {
}
