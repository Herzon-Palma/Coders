package com.uamishop.ventas.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class CarritoId implements Serializable {
    private UUID id;

    public CarritoId() { this.id = UUID.randomUUID(); }
    public CarritoId(UUID id) { this.id = id; }
    
    public UUID getValue() { return id; }

    @Override
    public boolean equals(Object o) { return id.equals(((CarritoId)o).id); }
    @Override
    public int hashCode() { return id.hashCode(); }
}
