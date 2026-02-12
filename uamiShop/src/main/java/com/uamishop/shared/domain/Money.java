package com.uamishop.shared.domain;

import java.math.BigDecimal;


public record Money(BigDecimal cantidad, String moneda) {
    public static Money zero() {
        return new Money(BigDecimal.ZERO, "MXN");
    }

    public static Money pesos(double cantidad) {
        return new Money(BigDecimal.valueOf(cantidad), "MXN");
    }
    
    public Money multiplicar(int factor) { // Soluciona error en ItemCarrito
        return new Money(this.cantidad.multiply(BigDecimal.valueOf(factor)), this.moneda);
    }

    public Money sumar(Money otro) {
        validarMoneda(otro);
        return new Money(this.cantidad.add(otro.cantidad), this.moneda);
    }

    public Money restar(Money otro) {
        validarMoneda(otro);
        BigDecimal resultado = this.cantidad.subtract(otro.cantidad);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El resultado de una resta no puede ser negativo"); // RN-VO-02
        }
        return new Money(resultado, this.moneda);
    }

    private void validarMoneda(Money otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden sumar montos de diferentes monedas"); // RN-VO-01
        }
    }
}