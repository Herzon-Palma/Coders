package com.uamishop.catalogo.api;

import com.uamishop.shared.domain.Money;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogoApi {

    Optional<ProductoResumen> buscarProducto(UUID productoId);
    List<ProductoResumen> buscarProductos(List<UUID> productoIds);
    boolean existeProducto(UUID productoId); //Puedes ser que está sea redundante con buscarProducto, pero a veces es útil para validaciones rápidas
    boolean estaDisponible(UUID productoId);// Tambien está
    Optional<Money> obtenerPrecio(UUID productoId);// Está tambien puede ser redundante ya que al buscar el producto ya obtienes el precio, pero a veces es útil para consultas rápidas sin necesidad de cargar toda la info del producto
    boolean existeCategoria(UUID categoriaId);

}
