package com.uamishop.ventas.domain.exception;

/**
 * Excepción de regla de negocio del BC Ventas.
 * Incluye el identificador de la regla violada (e.g. "RN-VEN-01").
 */
public class ReglaNegocioVentasException extends VentasDomainException {

    private final String reglaId;

    public ReglaNegocioVentasException(String reglaId, String message) {
        super("[" + reglaId + "] " + message);
        this.reglaId = reglaId;
    }

    public String reglaId() {
        return reglaId;
    }
}
