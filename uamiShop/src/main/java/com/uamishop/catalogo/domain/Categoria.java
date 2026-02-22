package com.uamishop.catalogo.domain;

import java.util.Objects;
import jakarta.persistence.*;
import com.uamishop.catalogo.controller.dto.CategoriaResponse;

@Entity
@Table(name = "categorias")
public class Categoria {
    @EmbeddedId
    private final Categoriaid id;
    private String nombre;
    private String descripcion;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "categoria_padre_id"))
    private Categoriaid categoriaPadreId;

    private Categoria() {
        // Esto lo ocupa JPA, no lo usaremos directamente, y es necesario para evitar error de final en id
        this.id = null;
    }

    private Categoria(Categoriaid id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    
    public static Categoria crear(Categoriaid id, String nombre, String descripcion) {
    return new Categoria(id, nombre, descripcion);
    }

    public void asignarPadre(Categoriaid padreId) {
        this.categoriaPadreId = padreId; 
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion; 
    }

    public CategoriaResponse toResponse() {
    return new CategoriaResponse(
        this.id.valor(),
        this.nombre,
        this.descripcion,
        // Si categoriaPadreId es Categoriaid, extraemos el valor UUID [cite: 91, 93]
        this.categoriaPadreId != null ? this.categoriaPadreId.valor() : null
    );
}

    // Getters
    public Categoriaid getId() { return id; }
    public String getNombre() { return nombre; }
    public Categoriaid getCategoriaPadreId() { return categoriaPadreId; }
}