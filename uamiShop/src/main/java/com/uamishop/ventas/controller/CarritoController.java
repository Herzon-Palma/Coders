package com.uamishop.ventas.controller;

import com.uamishop.catalogo.domain.Productoid;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.ProductoRef;
import com.uamishop.ventas.service.CarritoService;
import com.uamishop.ventas.controller.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> crear(@RequestBody UUID clienteId) {
        Carrito carrito = carritoService.crear(new ClienteId(clienteId));
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable UUID id) {
        Carrito carrito = carritoService.obtenerCarrito(new CarritoId(id));
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable UUID id, 
            @RequestBody CarritoRequest request) {
        
        // En una implementación real, buscarías el producto en el catálogo para llenar el ProductoRef
        ProductoRef ref = new ProductoRef(new Productoid(request.productoId()), "Producto", "SKU-TEMP", null);
        // El precio vendría del servicio de catálogo
        Money precio = new Money(new java.math.BigDecimal("100.00"), "MXN"); 

        Carrito carrito = carritoService.agregarProducto(
                new CarritoId(id), ref, request.cantidad(), precio);
        
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PutMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> modificarCantidad(
            @PathVariable UUID id,
            @PathVariable UUID productoId,
            @RequestBody Integer nuevaCantidad) {
        
        Carrito carrito = carritoService.modificarCantidad(
                new CarritoId(id), new Productoid(productoId), nuevaCantidad);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @DeleteMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable UUID id,
            @PathVariable UUID productoId) {
        
        Carrito carrito = carritoService.eliminarProducto(
                new CarritoId(id), new Productoid(productoId));
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> vaciar(@PathVariable UUID id) {
        Carrito carrito = carritoService.vaciar(new CarritoId(id));
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CarritoResponse> iniciarCheckout(@PathVariable UUID id) {
        Carrito carrito = carritoService.iniciarCheckout(new CarritoId(id));
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    // Utilizamos un método privado para mapear el Carrito a una respuesta DTO, evitando exponer la entidad directamente
    private CarritoResponse mapToResponse(Carrito c) {
        var itemsResponse = c.getItems().stream()
                .map(item -> new ItemResponse(
                        item.getProductoRef().productoid().getValue(),
                        item.getProductoRef().nombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario().cantidad(),
                        item.calcularSubtotal().cantidad()
                )).collect(Collectors.toList());

        return new CarritoResponse(
                c.getId().getValue(),
                c.getClienteId().getValue(),
                c.getEstado().name(),
                itemsResponse,
                c.calcularTotal().cantidad(),
                c.calcularTotal().moneda()
        );
    }
}