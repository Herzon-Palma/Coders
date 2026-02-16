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

    @PostMapping
    public ResponseEntity<Carrito> crear(@RequestBody UUID clienteId) {
        return ResponseEntity.ok(carritoService.crear(new ClienteId(clienteId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(id));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<Carrito> agregarProducto(@PathVariable UUID id, @RequestBody AddProductoRequest request) {
        return ResponseEntity.ok(carritoService.agregarProducto(
                id, request.getProducto(), request.getCantidad(), request.getPrecio()));
    }

    @PatchMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Carrito> modificarCantidad(
            @PathVariable UUID id, 
            @PathVariable UUID productoId, 
            @RequestBody int cantidad) {
        return ResponseEntity.ok(carritoService.modificarCantidad(id, productoId, cantidad));
    }

    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable UUID id, @PathVariable UUID productoId) {
        carritoService.eliminarProducto(id, productoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<Carrito> iniciarCheckout(@PathVariable UUID id) {
        return ResponseEntity.ok(carritoService.iniciarCheckout(id));
    }
}
