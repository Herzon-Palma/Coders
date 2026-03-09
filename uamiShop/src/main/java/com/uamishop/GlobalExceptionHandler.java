package com.uamishop;

import com.uamishop.ordenes.domain.OrdenException;
import com.uamishop.ordenes.domain.exception.ReglaNegocioException;
import com.uamishop.shared.domain.exception.DomainException;
import com.uamishop.shared.domain.exception.ResourceNotFoundException;
import com.uamishop.ventas.domain.exception.CarritoException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────
    // Recurso no encontrado (producto, carrito, orden, categoría)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(404, e.getMessage()));
    }

    // ── 422 Unprocessable Entity ──────────────────────────
    // Reglas de negocio violadas (RN-ORD-XX, RN-VEN-XX)
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> handleReglaNegocio(ReglaNegocioException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError(422, e.getMessage()));
    }

    // ── 409 Conflict ──────────────────────────────────────
    // Conflicto de estado (ej: carrito en CHECKOUT, orden ya pagada)
    @ExceptionHandler(CarritoException.class)
    public ResponseEntity<ApiError> handleCarritoException(CarritoException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(409, e.getMessage()));
    }

    @ExceptionHandler(OrdenException.class)
    public ResponseEntity<ApiError> handleOrdenException(OrdenException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(409, e.getMessage()));
    }

    // ── 400 Bad Request ───────────────────────────────────
    // Datos de entrada inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, e.getMessage()));
    }

    // Errores de validación de @Valid (Jakarta Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Error de validación");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, mensaje));
    }

    // DomainException genérica (las que no son ResourceNotFoundException ni
    // ReglaNegocio)
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, e.getMessage()));
    }

    // ── 500 Internal Server Error ─────────────────────────
    // Cualquier otra excepción no prevista
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(500, "Error interno: " + e.getMessage()));
    }
}
