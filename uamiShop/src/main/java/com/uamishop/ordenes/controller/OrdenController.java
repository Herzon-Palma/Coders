package com.uamishop.ordenes.controller;

import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.domain.OrdenId;
import com.uamishop.ordenes.domain.InfoEnvio;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;
import com.uamishop.shared.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "API para la gestión del ciclo de vida de las órdenes")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una nueva orden", description = "Permite crear una orden en estado PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Violación de reglas de negocio", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<Orden> crear(@Valid @RequestBody CrearOrdenRequest request) {
        return ResponseEntity.ok(service.crearOrden(
                request.clienteId(),
                request.direccion(),
                request.items()));
    }

    @Operation(summary = "Obtener una orden por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Confirmar una orden")
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Orden> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarOrden(id));
    }

    @Operation(summary = "Procesar el pago de una orden")
    @PostMapping("/{id}/pagar")
    public ResponseEntity<Orden> pagar(@PathVariable UUID id, @NotBlank @RequestBody String referencia) {
        return ResponseEntity.ok(service.pagarOrden(id, referencia));
    }

    @Operation(summary = "Marcar orden como enviada")
    @PostMapping("/{id}/enviar")
    public ResponseEntity<Orden> enviar(@PathVariable UUID id, @Valid @RequestBody InfoEnvio infoEnvio) {
        return ResponseEntity.ok(service.enviarOrden(id, infoEnvio));
    }

    @Operation(summary = "Marcar orden como entregada")
    @PostMapping("/{id}/entregar")
    public ResponseEntity<Orden> entregar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.entregarOrden(id));
    }

    @Operation(summary = "Cancelar una orden")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Orden> cancelar(@PathVariable UUID id, @NotBlank @RequestBody String motivo) {
        return ResponseEntity.ok(service.cancelarOrden(id, motivo));
    }
}

// DTOs con validaciones
record CrearOrdenRequest(
        @NotNull(message = "El clienteId es obligatorio") UUID clienteId,

        @NotNull(message = "La dirección de envío es obligatoria") @Valid DireccionEnvio direccion,

        @NotEmpty(message = "La orden debe tener al menos un ítem") @Valid List<OrdenService.ItemDto> items) {
}
