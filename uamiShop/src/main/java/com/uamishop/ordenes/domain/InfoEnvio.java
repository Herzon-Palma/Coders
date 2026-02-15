package com.uamishop.ordenes.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object: información del envío cuando la orden es despachada.
 * Contiene proveedor logístico, número de guía y fecha estimada de entrega.
 * - RN-ORD-11: Debe proporcionarse el número de guía.
 * - RN-ORD-12: El número de guía debe tener al menos 10 caracteres.
 */
public record InfoEnvio(
        String proveedorLogistico,
        String numeroGuia,
        LocalDateTime fechaEstimadaEntrega) {

    public InfoEnvio {
        Objects.requireNonNull(proveedorLogistico, "Proveedor logístico requerido");
        Objects.requireNonNull(numeroGuia, "Número de guía requerido"); // RN-ORD-11
        Objects.requireNonNull(fechaEstimadaEntrega, "Fecha estimada de entrega requerida");

        if (proveedorLogistico.isBlank()) {
            throw new IllegalArgumentException("Proveedor logístico no puede estar vacío");
        }
        if (numeroGuia.isBlank()) {
            throw new IllegalArgumentException("Número de guía no puede estar vacío"); // RN-ORD-11
        }
        if (numeroGuia.trim().length() < 10) {
            throw new IllegalArgumentException(
                    "El número de guía debe tener al menos 10 caracteres"); // RN-ORD-12
        }
    }

    /**
     * Genera una URL de rastreo ficticia con el número de guía.
     */
    public String generarUrlRastreo() {
        return String.format("https://rastreo.%s.com/guia/%s",
                proveedorLogistico.toLowerCase().replaceAll("\\s+", ""),
                numeroGuia);
    }
}
