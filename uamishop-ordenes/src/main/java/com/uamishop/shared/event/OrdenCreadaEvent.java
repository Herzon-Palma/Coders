package com.uamishop.shared.event;

import java.time.Instant;
import java.util.UUID;

public record OrdenCreadaEvent(
    UUID eventoId,
    Instant occurredAt,
    UUID ordenId,
    UUID carritoId,
    UUID clienteId
) {}
