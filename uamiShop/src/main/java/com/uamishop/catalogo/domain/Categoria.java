package com.uamishop.catalogo.domain;

import java.util.Objects;

public class Categoria {
    private final Categoriaid id;
    private String nombre;
    private String descripcion;
    private Categoriaid categoriaPadreId; // Referencia a la categoría padre [cite: 41, 96]

    private Categoria(Categoriaid id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static Categoria crear(String nombre, String descripcion) {
        Objects.requireNonNull(nombre, "El nombre de la categoría es obligatorio");
        return new Categoria(Categoriaid.generar(), nombre, descripcion);     }


    public void asignarPadre(Categoriaid padreId) {
        this.categoriaPadreId = padreId; 
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion; 
    }

    // Getters
    public Categoriaid getId() { return id; }
    public String getNombre() { return nombre; }
    public Categoriaid getCategoriaPadreId() { return categoriaPadreId; }
}