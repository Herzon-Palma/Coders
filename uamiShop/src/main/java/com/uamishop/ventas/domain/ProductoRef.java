package ventas.domain;

public class ProductoRef {
    private final String productoId;
    private final String nombre;
    private final String sku;

    public ProductoRef(String productoId, String nombre, String sku) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.sku = sku;
    }

    public String getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public String getSku() { return sku; }

}
