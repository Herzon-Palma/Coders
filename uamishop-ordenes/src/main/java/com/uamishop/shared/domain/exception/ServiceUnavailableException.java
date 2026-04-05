package com.uamishop.shared.domain.exception;

// Se crea una excepción para cuando un servicio externo no está disponible
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
