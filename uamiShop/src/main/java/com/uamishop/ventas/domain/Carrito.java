@Entity
public class Carrito {
    @Id
    private UUID id = UUID.randomUUID();
    private UUID clienteId;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();
    
    private String estado; // ACTIVO, CHECKOUT, PROCESADO

    public Carrito() {}
    public Carrito(UUID clienteId) {
        this.clienteId = clienteId;
        this.estado = "ACTIVO";
    }

    public void agregarProducto(UUID productoId, String nombre, int cantidad, Money precio) {
        if (!"ACTIVO".equals(this.estado)) throw new DomainException("Carrito no editable");
        
        items.stream()
            .filter(i -> i.getProductoId().equals(productoId))
            .findFirst()
            .ifPresentOrElse(
                item -> item.actualizarCantidad(item.getCantidad() + cantidad),
                () -> items.add(new ItemCarrito(productoId, nombre, cantidad, precio))
            );
    }

    public void iniciarCheckout() {
        if (items.isEmpty()) throw new DomainException("No se puede hacer checkout de un carrito vacío");
        this.estado = "CHECKOUT";
    }
}
