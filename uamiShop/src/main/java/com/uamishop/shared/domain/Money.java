package com.uamishop.shared.domain;



public class Money {
    private double amount;
    private String currency;

    public Money(double amount, String currency) {
        if(amount < 0 || currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("El monto y la moneda no pueden ser nulos o vacíos");
        }
        this.amount = amount;
        this.currency = currency;
    
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("No se pueden sumar cantidades con diferentes monedas");
        }
        return new Money(this.amount + other.amount, this.currency);
    }

    public double getmonto() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
