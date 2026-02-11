package ventas.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
public class CarritoId implements Serializable {
    private UUID id;

    protected CarritoId() {}

    public CarritoId(UUID id) {
        this.id = id;
    }

    public static CarritoId random() {
        return new CarritoId(UUID.randomUUID());
    }
    
    public static CarritoId of(UUID id) {
        return new CarritoId(id);
    }
}
