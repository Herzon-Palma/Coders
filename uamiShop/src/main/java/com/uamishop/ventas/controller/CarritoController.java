package com.uamishop.ventas.controller;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;
import com.uamishop.ApiError;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.shared.domain.ProductoRef;
import com.uamishop.ventas.service.CarritoService;
import com.uamishop.ventas.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carritos")
@Tag(name = "Ventas: Carrito", description = "API para la gestión del carrito de compras")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(summary = "Crear un carrito para un cliente")
    @PostMapping
    public ResponseEntity<CarritoResponse> crear(@RequestBody ClienteId clienteId) {
        Carrito carrito = carritoService.crear(clienteId);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Obtener un carrito por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.obtenerCarrito(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Agregar producto al carrito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Error de lógica de negocio", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable CarritoId id,
            @Valid @RequestBody CarritoRequest request) {

        ProductoRef ref = new ProductoRef(new Productoid(request.productoId()), "Producto", "SKU-TMP");
        Money precio = new Money(new java.math.BigDecimal("100.00"), "MXN");

        Carrito carrito = carritoService.agregarProducto(
                id, ref, request.cantidad(), precio);

        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Modificar cantidad de un producto")
    @PutMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> modificarCantidad(
            @PathVariable CarritoId id,
            @PathVariable Productoid productoId,
            @RequestBody Integer nuevaCantidad) {

        Carrito carrito = carritoService.modificarCantidad(
                id, productoId, nuevaCantidad);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Eliminar producto del carrito")
    @DeleteMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable CarritoId id,
            @PathVariable Productoid productoId) {

        Carrito carrito = carritoService.eliminarProducto(
                id, productoId);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Vaciar el carrito")
    @DeleteMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> vaciar(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.vaciar(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

    @Operation(summary = "Iniciar checkout del carrito")
    @PostMapping("/{id}/checkout")
    public ResponseEntity<CarritoResponse> iniciarCheckout(@PathVariable CarritoId id) {
        Carrito carrito = carritoService.iniciarCheckout(id);
        return ResponseEntity.ok(mapToResponse(carrito));
    }

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
