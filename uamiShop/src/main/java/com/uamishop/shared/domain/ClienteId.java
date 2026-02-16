package com.uamishop.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ClienteId implements Serializable {
    private UUID id;

    public ClienteId() {}

    public ClienteId(UUID id) {
        this.id = id;
    }

    public static ClienteId of(UUID id) {
        return new ClienteId(id);
    }
    
    public UUID getValue() { return id; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteId that = (ClienteId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
