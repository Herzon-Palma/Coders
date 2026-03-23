package com.uamishop.ordenes.controller;

import java.util.List;
import java.util.UUID;

import com.uamishop.ordenes.service.OrdenService;

public record OrdenResponse(
        UUID ordenId,
        String estadoOrden,
        UUID clienteId,
        List<OrdenService.ItemDto> items) {
}
