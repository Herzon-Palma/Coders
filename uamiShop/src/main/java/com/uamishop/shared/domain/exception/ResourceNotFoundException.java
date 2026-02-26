package com.uamishop.shared.domain.exception;

/**
 * Se lanza cuando un recurso solicitado no existe (producto, carrito, orden,
 * categoría, etc.)
 * Se mapea a HTTP 404 Not Found en el GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
