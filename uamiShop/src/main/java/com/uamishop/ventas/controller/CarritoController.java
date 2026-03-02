package com.uamishop.ventas.controller;

import com.uamishop.ApiError;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.ProductoId;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.service.CarritoService;
import com.uamishop.ventas.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/carritos")
@Tag(name = "Carritos", description = "API para gestionar carritos de compras del subdominio de Ventas")
public class CarritoController {

        private final CarritoService carritoService;

        public CarritoController(CarritoService carritoService) {
                this.carritoService = carritoService;
        }

        @Operation(summary = "Crear carrito", description = "Crea un nuevo carrito de compras para un cliente. El carrito se inicializa en estado ACTIVO.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carrito creado exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Error de validación en los datos del cliente", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping
        public ResponseEntity<CarritoResponse> crear(@Valid @RequestBody ClienteId clienteId) {
                Carrito carrito = carritoService.crear(clienteId);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Obtener carrito por ID", description = "Obtiene los detalles de un carrito existente por su ID, incluyendo sus items y total.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carrito encontrado exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<CarritoResponse> obtener(
                        @Parameter(description = "ID del carrito a consultar") @PathVariable CarritoId id) {
                Carrito carrito = carritoService.obtenerCarrito(id);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito. Si el producto ya existe, incrementa la cantidad. "
                        + "Reglas: cantidad entre 1-10 (RN-VEN-01, RN-VEN-02), máximo 20 productos distintos (RN-VEN-03). "
                        + "El carrito debe estar en estado ACTIVO.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto agregado al carrito exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Error de validación (ej: cantidad fuera de rango, carrito no activo)", content = @Content(schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/items")
        public ResponseEntity<CarritoResponse> agregarProducto(
                        @Parameter(description = "ID del carrito") @PathVariable CarritoId id,
                        @Valid @RequestBody CarritoRequest request) {

                Carrito carrito = carritoService.agregarProducto(
                                id, new ProductoId(request.productoId()), request.cantidad());

                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Modificar cantidad de un producto", description = "Modifica la cantidad de un producto existente en el carrito. "
                        + "La nueva cantidad debe estar entre 1 y 10 (RN-VEN-02, RN-VEN-05). "
                        + "El carrito debe estar en estado ACTIVO (RN-VEN-06).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad modificada exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Error de validación o producto no encontrado en el carrito", content = @Content(schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @PutMapping("/{id}/items/{productoId}")
        public ResponseEntity<CarritoResponse> modificarCantidad(
                        @Parameter(description = "ID del carrito") @PathVariable CarritoId id,
                        @Parameter(description = "ID del producto a modificar") @PathVariable ProductoId productoId,
                        @NotNull(message = "La nueva cantidad es obligatoria") @Min(value = 1, message = "La cantidad mínima es 1 (RN-VEN-05)") @Max(value = 10, message = "La cantidad máxima es 10 (RN-VEN-02)") @RequestBody Integer nuevaCantidad) {

                Carrito carrito = carritoService.modificarCantidad(
                                id, productoId, nuevaCantidad);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito (RN-VEN-07). "
                        + "El producto debe existir en el carrito (RN-VEN-08). "
                        + "El carrito debe estar en estado ACTIVO.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto eliminado del carrito exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Producto no encontrado en el carrito o carrito no modificable", content = @Content(schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @DeleteMapping("/{id}/items/{productoId}")
        public ResponseEntity<CarritoResponse> eliminarProducto(
                        @Parameter(description = "ID del carrito") @PathVariable CarritoId id,
                        @Parameter(description = "ID del producto a eliminar") @PathVariable ProductoId productoId) {

                Carrito carrito = carritoService.eliminarProducto(
                                id, productoId);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos y descuentos del carrito (RN-VEN-09). "
                        + "El carrito debe estar en estado ACTIVO.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "El carrito no está en estado ACTIVO", content = @Content(schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @DeleteMapping("/{id}/items")
        public ResponseEntity<CarritoResponse> vaciar(
                        @Parameter(description = "ID del carrito a vaciar") @PathVariable CarritoId id) {
                Carrito carrito = carritoService.vaciar(id);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        @Operation(summary = "Iniciar checkout", description = "Inicia el proceso de checkout del carrito. "
                        + "Requiere: carrito ACTIVO (RN-VEN-11), al menos un producto (RN-VEN-10), "
                        + "y total mayor a $50 MXN (RN-VEN-12). Cambia el estado a CHECKOUT.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Checkout iniciado exitosamente, carrito pasa a estado CHECKOUT", content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "No se cumplen las condiciones para checkout (carrito vacío, total insuficiente, estado inválido)", content = @Content(schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/checkout")
        public ResponseEntity<CarritoResponse> iniciarCheckout(
                        @Parameter(description = "ID del carrito para iniciar checkout") @PathVariable CarritoId id) {
                Carrito carrito = carritoService.iniciarCheckout(id);
                return ResponseEntity.ok(mapToResponse(carrito));
        }

        // Utilizamos un método privado para mapear el Carrito a una respuesta DTO,
        // evitando exponer la entidad directamente
        private CarritoResponse mapToResponse(Carrito c) {
                var itemsResponse = c.getItems().stream()
                                .map(item -> new ItemResponse(
                                                item.getProductoRef().productoId().getValue(),
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
