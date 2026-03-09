package com.uamishop.ordenes.domain.exception;

/**
 * Excepción para violaciones explícitas de reglas RN-ORD-XX.
 */
public class ReglaNegocioException extends OrdenDomainException {

    private final String reglaId;

    public ReglaNegocioException(String reglaId, String message) {
        super(reglaId + ": " + message);
        this.reglaId = reglaId;
    }

    public String reglaId() {
        return reglaId;
    }
}
