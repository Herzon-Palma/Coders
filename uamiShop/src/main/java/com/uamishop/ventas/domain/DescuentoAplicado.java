package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class DescuentoAplicado {
    private String codigoCupon;
    @Embedded
    private Money montoDescuento;

    protected DescuentoAplicado() {}

    public DescuentoAplicado(String codigo, Money monto) {
        this.codigoCupon = codigo;
        this.montoDescuento = monto;
    }
}
