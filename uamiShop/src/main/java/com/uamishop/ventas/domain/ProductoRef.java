package ventas.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor // JPA
public class ProductoRef {
    private UUID productoId;
    private String nombre;
    private String sku;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cantidad", column = @Column(name = "precio_unitario")),
        @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private Money precioUnitario;

    public ProductoRef(UUID productoId, String nombre, String sku, Money precioUnitario) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.sku = sku;
        this.precioUnitario = precioUnitario;
    }
}
