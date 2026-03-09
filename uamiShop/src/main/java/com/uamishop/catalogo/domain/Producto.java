package com.uamishop.catalogo.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.Productoid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.persistence.*;
import com.uamishop.catalogo.controller.dto.ProductoResponse;
import com.uamishop.shared.domain.exception.DomainException;


@Entity
@Table(name = "productos")
public class Producto {
    private static final Pattern PATRON_SKU = Pattern.compile("^[A-Z]{3}-\\d{3}$");

    //Necesitamos un ID para cada producto, lo generaremos automáticamente
    @EmbeddedId
    private Productoid id;

    private String nombre;
    private String descripcion;
    private String sku;
    
    @Embedded
    private Money precio;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "categoria_id"))
    private final Categoriaid categoriaId;

    @ElementCollection
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "producto_id"))
    private List<Imagen> imagenes;

    private boolean disponible;

    private Producto() {
        // Esto lo ocupa JPA, no lo usaremos directamentte, y es necesario para evitar error de final en categoriaId
        this.categoriaId = null; // Necesario para evitar error de final
    }

    public Producto(Productoid id, String nombre, String descripcion, String sku, Money precio, Categoriaid categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.sku = sku;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.imagenes = new ArrayList<>();
        this.disponible = false;
    }

    public static Producto crear(String nombre, String descripcion, String sku, Money precio, Categoriaid categoriaId) {
        if (nombre == null || nombre.length() < 3 || nombre.length() > 100) 
            throw new DomainException("Nombre inválido");
        if (precio.cantidad().compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new DomainException("Precio debe ser mayor a cero");
        if (descripcion != null && descripcion.length() > 500)
            throw new DomainException("Descripción demasiado larga");
        if (sku == null || !PATRON_SKU.matcher(sku.trim().toUpperCase()).matches())
            throw new DomainException("SKU debe tener formato AAA-000 (3 letras mayúsculas, guion, 3 dígitos)");

        return new Producto(Productoid.generar(), nombre, descripcion, sku.trim().toUpperCase(), precio, categoriaId);
    }

    public void cambiarPrecio(Money nuevoPrecio) {
        if (nuevoPrecio.cantidad().compareTo(java.math.BigDecimal.ZERO) < 0)
            throw new DomainException("Precio no puede ser negativo");
        
        java.math.BigDecimal limite = precio.cantidad().multiply(java.math.BigDecimal.valueOf(1.5));
        if (nuevoPrecio.cantidad().compareTo(limite) > 0)
            throw new DomainException("El incremento no puede superar el 50%");

        this.precio = nuevoPrecio;
    }

    public void agregarImagen(Imagen imagen) {
        if (this.imagenes.size() >= 5) throw new DomainException("Máximo 5 imágenes");
        this.imagenes.add(imagen);
    }

    public void activar() {
        if (imagenes.isEmpty()) throw new DomainException("Debe tener al menos una imagen");
        if (precio.cantidad().compareTo(java.math.BigDecimal.ZERO) <= 0) 
            throw new DomainException("Precio debe ser mayor a cero");
        this.disponible = true;
    }

    public void desactivar() {
        this.disponible = false;
    }

    public void removerImagen(Imagen imagen) {
        this.imagenes.remove(imagen);
    }

    public void actualizarInformacion(String nombre, String descripcion) {
        if (nombre == null || nombre.length() < 3 || nombre.length() > 100) 
            throw new DomainException("Nombre inválido");
        if (descripcion != null && descripcion.length() > 500)
            throw new DomainException("Descripción demasiado larga");

        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public ProductoResponse toResponse() {
        return new ProductoResponse(
            this.id.valor(),
            this.nombre,
            this.descripcion,
            this.sku,
            this.precio.cantidad(),
            this.precio.moneda(),
            this.categoriaId.valor(),
            this.disponible
        );
    }

    public void actualizar(String nombre, String descripcion, Money precio) {
        actualizarInformacion(nombre, descripcion);
        cambiarPrecio(precio);
    }

    // Getters necesarios para JPA y pruebas
    public Productoid getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Money getPrecio() { return precio; }
    public String getSku() { return sku; }
    public Categoriaid getCategoriaId() { return categoriaId; }
    public List<Imagen> getImagenes() { return imagenes; }
    public boolean isDisponible() { return disponible; }


}
