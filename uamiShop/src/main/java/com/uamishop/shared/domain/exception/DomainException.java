package com.uamishop.shared.domain.exception;

/**
 * Base exception for domain-related errors.
 * Used when domain rules or invariants are violated.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
