package com.uamishop.ventas.controller;

import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // GET
    @GetMapping
    public ResponseEntity<Carrito> obtenerCarrito(@RequestParam UUID clienteId) {
        return ResponseEntity.ok(service.obtenerCarritoActivo(clienteId));
    }

    // POST
    @PostMapping("/{id}/productos")
    public ResponseEntity<Carrito> agregarProducto(
            @PathVariable UUID id, 
            @RequestBody AgregarProductoRequest request) {
        
        Carrito carrito = service.agregarProducto(
            id, 
            request.productoId(), 
            request.nombre(), 
            request.cantidad(), 
            request.precio()
        );
        return ResponseEntity.ok(carrito);
    }

    // DELETE
    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable UUID id, @PathVariable UUID productoId) {
        service.eliminarProducto(id, productoId);
        return ResponseEntity.noContent().build();
    }

    // POST
    @PostMapping("/{id}/checkout")
    public ResponseEntity<Carrito> checkout(@PathVariable UUID id) {
        return ResponseEntity.ok(service.checkout(id));
    }
}

record AgregarProductoRequest(UUID productoId, String nombre, int cantidad, double precio) {}
