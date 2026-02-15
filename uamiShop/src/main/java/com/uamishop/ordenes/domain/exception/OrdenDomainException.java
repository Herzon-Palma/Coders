package com.uamishop.ordenes.domain.exception;

import com.uamishop.shared.domain.exception.DomainException;

/**
 * Excepción base del Bounded Context Órdenes.
 */
public class OrdenDomainException extends DomainException {

    public OrdenDomainException(String message) {
        super(message);
    }

    public OrdenDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
