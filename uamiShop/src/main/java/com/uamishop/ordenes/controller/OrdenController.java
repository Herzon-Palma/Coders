package com.uamishop.ordenes.controller;

import com.uamishop.ordenes.domain.Orden;
import com.uamishop.ordenes.domain.InfoEnvio;
import com.uamishop.ordenes.service.OrdenService;
import com.uamishop.shared.domain.DireccionEnvio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear nueva orden", description = "Permite a un cliente crear una nueva orden de compra, devuelve la orden generada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos de la petición")
    })
    public ResponseEntity<Orden> crear(@Valid @RequestBody CrearOrdenRequest request) {
        return ResponseEntity.ok(service.crearOrden(
                request.clienteId(),
                request.direccion(),
                request.items()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden", description = "Busca una orden en el sistema usando su identificador único (UUID).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden obtenida exitosamente")
    })
    public ResponseEntity<Orden> obtener(
            @Parameter(description = "ID único de la orden") @PathVariable UUID id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar orden", description = "Mueve una orden del estado PENDIENTE al estado CONFIRMADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden confirmada exitosamente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio violada - la orden no estaba en estado PENDIENTE")
    })
    public ResponseEntity<Orden> confirmar(
            @Parameter(description = "ID único de la orden a confirmar") @PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarOrden(id));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Procesar pago de la orden", description = "Mueve una orden CONFIRMADA al estado PAGO_PROCESADO usando la referencia del pago.")
    public ResponseEntity<Orden> pagar(
            @Parameter(description = "ID único de la orden que fue pagada") @PathVariable UUID id,
            @Parameter(description = "Referencia del pago aprobatoria") @RequestBody String referencia) {
        return ResponseEntity.ok(service.pagarOrden(id, referencia));
    }

    @PostMapping("/{id}/enviar")
    @Operation(summary = "Marcar orden como enviada", description = "Agrega número de guía a la orden y la deja EN_TRANSITO/ENVIADA.")
    public ResponseEntity<Orden> enviar(
            @Parameter(description = "ID de la orden a ser marcada como enviada") @PathVariable UUID id,
            @Valid @RequestBody InfoEnvio infoEnvio) {
        return ResponseEntity.ok(service.enviarOrden(id, infoEnvio));
    }

    @PostMapping("/{id}/entregar")
    @Operation(summary = "Marcar orden como entregada", description = "Mueve la orden desde ENVIADA al estado final ENTREGADA.")
    public ResponseEntity<Orden> entregar(
            @Parameter(description = "ID único de la orden entregada") @PathVariable UUID id) {
        return ResponseEntity.ok(service.entregarOrden(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar orden", description = "Mueve la orden a CANCELADA proveyendo un motivo de cancelación obligatorio.")
    public ResponseEntity<Orden> cancelar(
            @Parameter(description = "ID de la orden a cancelar") @PathVariable UUID id,
            @Parameter(description = "Justificación/motivo de cancelación") @RequestBody String motivo) {
        return ResponseEntity.ok(service.cancelarOrden(id, motivo));
    }
}

// DTOs
record CrearOrdenRequest(
        @NotNull(message = "El ID del cliente es obligatorio") UUID clienteId,
        @NotNull(message = "La dirección de envío es obligatoria") @Valid DireccionEnvio direccion,
        @NotEmpty(message = "La orden no puede estar vacía, debe contener al menos 1 ítem") @Valid List<OrdenService.ItemDto> items) {
}

//DTO para response

