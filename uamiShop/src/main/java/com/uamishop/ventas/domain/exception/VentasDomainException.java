package com.uamishop.ventas.domain.exception;

import com.uamishop.shared.domain.exception.DomainException;

public class VentasDomainException extends DomainException {
    public VentasDomainException(String message) {
        super(message);
    }

    public VentasDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
