package com.uamishop.catalogo.domain;

import com.uamishop.shared.domain.Money;
import java.util.ArrayList;
import java.util.List;

public class Producto {
    private final Productoid id;
    private String nombre;
    private String descripcion;
    private Money precio;
    private final Categoriaid categoriaId;
    private List<Imagen> imagenes;
    private boolean disponible;

    private Producto(Productoid id, String nombre, String descripcion, Money precio, Categoriaid categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.imagenes = new ArrayList<>();
        this.disponible = false;
    }

    public static Producto crear(String nombre, String descripcion, Money precio, Categoriaid categoriaId) {
        // RN-CAT-01: Nombre entre 3 y 100 caracteres
        if (nombre == null || nombre.length() < 3 || nombre.length() > 100) 
            throw new RuntimeException("Nombre inválido");
        // RN-CAT-02: Precio mayor a cero
        if (precio.cantidad().compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Precio debe ser mayor a cero");
        // RN-CAT-03: Descripción máx 500
        if (descripcion != null && descripcion.length() > 500)
            throw new RuntimeException("Descripción demasiado larga");

        return new Producto(Productoid.generar(), nombre, descripcion, precio, categoriaId);
    }

    public void cambiarPrecio(Money nuevoPrecio) {
        // RN-CAT-04: No negativo
        if (nuevoPrecio.cantidad().compareTo(java.math.BigDecimal.ZERO) < 0)
            throw new RuntimeException("Precio no puede ser negativo");
        
        // RN-CAT-05: No incremento > 50%
        java.math.BigDecimal limite = precio.cantidad().multiply(java.math.BigDecimal.valueOf(1.5));
        if (nuevoPrecio.cantidad().compareTo(limite) > 0)
            throw new RuntimeException("El incremento no puede superar el 50%");

        this.precio = nuevoPrecio;
    }

    public void agregarImagen(Imagen imagen) {
        // RN-CAT-06: Máximo 5 imágenes
        if (this.imagenes.size() >= 5) throw new RuntimeException("Máximo 5 imágenes");
        this.imagenes.add(imagen);
    }

    public void activar() {
        // RN-CAT-09 y RN-CAT-10
        if (imagenes.isEmpty()) throw new RuntimeException("Debe tener al menos una imagen");
        if (precio.cantidad().compareTo(java.math.BigDecimal.ZERO) <= 0) 
            throw new RuntimeException("Precio debe ser mayor a cero");
        this.disponible = true;
    }
}
