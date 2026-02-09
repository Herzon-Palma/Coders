package ventas.domain;

import java.io.Serializable;
import java.util.UUID;

public class CarritoId implements Serializable {
    private final UUID value;

    private CarritoId(UUID value) {
        this.value = value;
    }

    public static CarritoId generar() {
        return new CarritoId(UUID.randomUUID());
    }
    
    public static CarritoId de(String uuid) {
        return new CarritoId(UUID.fromString(uuid));
    }

    public UUID getValue() {
        return value;
    }
}
