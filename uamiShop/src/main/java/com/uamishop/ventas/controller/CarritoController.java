package com.uamishop.ventas.controller;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.service.CarritoService;
import com.uamishop.ventas.controller.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Carrito> getCarrito(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(carritoService.obtenerOCrearCarrito(clienteId));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<Carrito> agregar(@PathVariable UUID id, @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(carritoService.agregarProducto(id, dto));
    }

    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @PathVariable UUID productoId) {
        carritoService.eliminarProducto(id, productoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<Carrito> checkout(@PathVariable UUID id) {
        return ResponseEntity.ok(carritoService.finalizarCompra(id));
    }
}

// DTO para la transferencia de datos
public record ProductoDTO(UUID productoId, String nombre, int cantidad, Money precio) {}
