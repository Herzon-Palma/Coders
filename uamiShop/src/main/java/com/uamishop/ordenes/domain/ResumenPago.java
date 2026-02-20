package com.uamishop.ordenes.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.time.LocalDateTime;

@Embeddable
public class ResumenPago {
    private String referenciaPago;
    private LocalDateTime fechaPago;
    
    @Embedded
    private Money montoPagado;

    protected ResumenPago() {}

    public ResumenPago(String referencia, Money monto) {
        this.referenciaPago = referencia;
        this.montoPagado = monto;
        this.fechaPago = LocalDateTime.now();
    }
    
    //Gets
    public String getReferenciaPago() { return referenciaPago; }
    public Money getMontoPagado() { return montoPagado; }
}
