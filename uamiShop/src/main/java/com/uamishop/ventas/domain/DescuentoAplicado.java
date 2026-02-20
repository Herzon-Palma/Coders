package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public record DescuentoAplicado(
    String codigo,
    @Enumerated(EnumType.STRING)
    TipoDescuento tipo,
    BigDecimal valor,
    Money montoDescuento
) {
}
