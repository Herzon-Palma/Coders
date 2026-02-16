package com.uamishop.ordenes.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class OrdenId implements Serializable {
    private UUID id;

    public OrdenId() { this.id = UUID.randomUUID(); }
    public OrdenId(UUID id) { this.id = id; }
    
    public UUID getValue() { return id; }

    @Override
    public boolean equals(Object o) { 
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((OrdenId)o).id); 
    }
    @Override
    public int hashCode() { return id.hashCode(); }
}
