package com.uamishop.ventas.domain;

import java.math.BigDecimal;
import com.uamishop.shared.domain.Money;

import jakarta.persistence.Embeddable;

@Embeddable
public record DescuentoAplicado(String codigo, TipoDescuento tipo, BigDecimal valor, Money montoDescuento) {
    public Money calcularDescuento(Money subtotal){
        if (tipo == TipoDescuento.PORCENTAJE) {
            return subtotal.multiplicar(valor.divide(BigDecimal.valueOf(100)));
        } else if (tipo == TipoDescuento.MONTO_FIJO) {
            return new Money(valor, subtotal.moneda());
        } else {
            throw new IllegalArgumentException("Tipo de descuento no soportado");
        }
    }

}
