package com.uamishop.shared.exception;

import com.uamishop.ordenes.domain.exception.ReglaNegocioException;
import com.uamishop.shared.domain.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import com.uamishop.ApiError;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Excepciones de reglas de negocio (HTTP 422)
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> handleReglaNegocioException(ReglaNegocioException ex, WebRequest request) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    // Excepciones de dominio generales (HTTP 400).
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex, WebRequest request) {
        log.warn("Error de dominio: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Domain Error", ex.getMessage(), request);
    }

    // Maneja errores de validación (@Valid | HTTP 400).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Error de validación: {}", details);
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Errores de validación: " + details, request);
    }

    // Maneja errores de argumentos ilegales (HTTP 400).
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<ApiError> handleIllegalArguments(RuntimeException ex, WebRequest request) {
        log.warn("Argumento o estado ilegal: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Argument/State", ex.getMessage(), request);
    }

    // Maneja excepciones genéricas no controladas (HTTP 500).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Error inesperado: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Ocurrió un error inesperado en el servidor", request);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String error, String message,
            WebRequest request) {
        ApiError apiError = new ApiError(
                status.value(),
                error,
                message,
                getPath(request));
        return new ResponseEntity<>(apiError, status);
    }

    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return null;
    }
}

