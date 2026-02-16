@Entity
public class ItemCarrito {
    @Id
    private UUID id = UUID.randomUUID();
    private UUID productoId;
    private String nombreProducto;
    private int cantidad;
    @Embedded
    private Money precioUnitario;

    protected ItemCarrito() {}

    public ItemCarrito(UUID productoId, String nombreProducto, int cantidad, Money precio) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precio;
    }

    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new DomainException("La cantidad debe ser mayor a 0");
        this.cantidad = nuevaCantidad;
    }

    public Money subtotal() {
        return new Money(precioUnitario.amount().multiply(BigDecimal.valueOf(cantidad)), precioUnitario.currency());
    }
}
