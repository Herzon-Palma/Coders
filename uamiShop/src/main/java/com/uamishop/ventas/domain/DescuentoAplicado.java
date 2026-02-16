package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object: descuento aplicado al carrito.
 * - RN-VEN-16: El descuento no puede ser mayor al 30% del subtotal.
 */
public record DescuentoAplicado(
        String codigo,
        String tipo,
        BigDecimal valor) {

    public static final BigDecimal PORCENTAJE_MAXIMO = new BigDecimal("30");

    public DescuentoAplicado {
        Objects.requireNonNull(codigo, "Código de descuento requerido");
        Objects.requireNonNull(tipo, "Tipo de descuento requerido");
        Objects.requireNonNull(valor, "Valor de descuento requerido");

        if (codigo.isBlank()) {
            throw new IllegalArgumentException("Código de descuento no puede estar vacío");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor del descuento debe ser mayor a cero");
        }
    }

    /**
     * Calcula el monto descontado sobre el subtotal dado.
     * Si el tipo es "PORCENTAJE", aplica el porcentaje.
     * Si el tipo es "FIJO", devuelve el monto fijo (sin exceder el subtotal).
     *
     * @param subtotal monto base sobre el cual calcular
     * @return monto de descuento como Money
     */
    public Money calcularDescuento(Money subtotal) {
        Objects.requireNonNull(subtotal, "Subtotal requerido para calcular descuento");

        BigDecimal montoDescuento;

        if ("PORCENTAJE".equalsIgnoreCase(tipo)) {
            montoDescuento = subtotal.cantidad()
                    .multiply(valor)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            // FIJO: el descuento es el valor, sin exceder el subtotal
            montoDescuento = valor.min(subtotal.cantidad());
        }

        return new Money(montoDescuento, subtotal.moneda());
    }
}
