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
    public LocalDateTime getFechaPago() { return fechaPago; }

    public ResumenPago conPagoAprobado(String referenciaPago2) {
        if (referenciaPago2 == null || referenciaPago2.isBlank()) {
            throw new OrdenException("Referencia de pago no puede ser vacía");
        }
        return new ResumenPago(referenciaPago2, this.montoPagado);
    }

    public static ResumenPago crear(String string) {
        String[] partes = string.split(";");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato inválido para ResumenPago. Se esperaba: referencia;fecha;monto");
        }
        String referencia = partes[0].trim();
        LocalDateTime fecha = LocalDateTime.parse(partes[1].trim());
        Money monto = Money.parse(partes[2].trim());
        ResumenPago resumen = new ResumenPago(referencia, monto);
        resumen.fechaPago = fecha; // Sobrescribe la fecha actual con la proporcionada
        return resumen;
    }
}
