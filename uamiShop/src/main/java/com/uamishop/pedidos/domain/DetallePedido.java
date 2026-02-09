package com.uamishop.pedidos.domain;
import com.uamishop.shared.domain.Money;

public class DetallePedido {
    private final String productoId;
    private final int cantidad;
    private final Money precioUnitario;
    private final String nombreProducto;

    public DetallePedido(String productoId, int cantidad, Money precioUnitario, String nombreProducto) {
        if(cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if(precioUnitario == null) {
            throw new IllegalArgumentException("El precio unitario no puede ser nulo");
        }
        if(nombreProducto == null || nombreProducto.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if(productoId == null || productoId.isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo o vacío");
        }
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nombreProducto = nombreProducto;
    }

    public Money subtotal(){
        return new Money( precioUnitario.getmonto() * cantidad, precioUnitario.getCurrency());
    }

}
