package com.uamishop.ventas.controller;

import com.uamishop.ventas.controller.dto.AgregarItemRequest;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ventas/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    public ResponseEntity<Carrito> iniciarSesionCarrito(@RequestParam UUID clienteId) {
        Carrito carrito = carritoService.crearOObtenerCarrito(clienteId);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<String> agregarItem(
            @PathVariable UUID id,
            @RequestBody AgregarItemRequest request) {
        
        carritoService.agregarProducto(
                id,
                request.productoId(),
                request.nombre(),
                request.precio(),
                request.cantidad()
        );
        return ResponseEntity.ok("Producto agregado correctamente");
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<String> realizarCheckout(@PathVariable UUID id) {
        carritoService.iniciarCheckout(id);
        return ResponseEntity.ok("Checkout iniciado correctamente");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable UUID id) {
        return ResponseEntity.ok(carritoService.obtenerPorId(id));
    }
    
    // Manejo de excepciones básico para respuesta HTTP
    @ExceptionHandler(com.uamishop.shared.exception.DomainException.class)
    public ResponseEntity<String> handleDomainException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
