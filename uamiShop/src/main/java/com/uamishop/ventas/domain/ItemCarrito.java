package ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.exception.DomainException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "items_carrito")
@Getter
public class ItemCarrito {

    @Id
    private UUID id;

    @Embedded
    private ProductoRef productoRef;

    private Integer cantidad;

    protected ItemCarrito() {} // JPA

    public ItemCarrito(ProductoRef productoRef, Integer cantidad) {
        this.id = UUID.randomUUID();
        this.productoRef = productoRef;
        setCantidad(cantidad);
    }

    public void incrementarCantidad(Integer extra) {
        setCantidad(this.cantidad + extra);
    }

    public void actualizarCantidad(Integer nuevaCantidad) {
        setCantidad(nuevaCantidad);
    }

    private void setCantidad(Integer cantidad) {
        if (cantidad <= 0) throw new DomainException("La cantidad debe ser mayor a 0");
        if (cantidad > 10) throw new DomainException("Máximo 10 unidades por producto");
        this.cantidad = cantidad;
    }

    public Money calcularSubtotal() {
        return productoRef.getPrecioUnitario().multiplicar(cantidad);
    }
}
