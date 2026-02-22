package com.uamishop.ventas.controller;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.service.CarritoService;
import com.uamishop.ventas.controller.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> crear(@RequestBody ClienteId clienteId) {
        Carrito carrito = carritoService.crear(clienteId);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.obtenerCarrito(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable CarritoId id,
            @RequestBody CarritoRequest request) {

        // En una implementación real, buscarías el producto en el catálogo para llenar
        // el ProductoRef
        ProductoRef ref = new ProductoRef(new Productoid(request.productoId()), "Producto", "SKU-TMP");
        // El precio vendría del servicio de catálogo
        Money precio = new Money(new java.math.BigDecimal("100.00"), "MXN");

        Carrito carrito = carritoService.agregarProducto(
                id, ref, request.cantidad(), precio);

        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PutMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> modificarCantidad(
            @PathVariable CarritoId id,
            @PathVariable Productoid productoId,
            @RequestBody Integer nuevaCantidad) {

        Carrito carrito = carritoService.modificarCantidad(
                id, productoId, nuevaCantidad);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @DeleteMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable CarritoId id,
            @PathVariable Productoid productoId) {

        Carrito carrito = carritoService.eliminarProducto(
                id, productoId);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> vaciar(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.vaciar(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CarritoResponse> iniciarCheckout(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.iniciarCheckout(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    // Utilizamos un método privado para mapear el Carrito a una respuesta DTO,
    // evitando exponer la entidad directamente
    private CarritoResponse mapToResponse(Carrito c) {
        var itemsResponse = c.getItems().stream()
                .map(item -> new ItemResponse(
                        item.getProductoRef().productoid().getValue(),
                        item.getProductoRef().nombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario().cantidad(),
                        item.calcularSubtotal().cantidad()))
                .collect(Collectors.toList());

        return new CarritoResponse(
                c.getId().getValue(),
                c.getClienteId().getValue(),
                c.getEstado().name(),
                itemsResponse,
                c.calcularTotal().cantidad(),
                c.calcularTotal().moneda());
    }
}