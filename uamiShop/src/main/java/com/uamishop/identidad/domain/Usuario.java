package com.uamishop.identidad.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import com.uamishop.shared.domain.DomainException;

public class Usuario {

    private final UUID id;
    private String nombre;
    private String email;
    private String contrasenaHash;
    private Rol rol;
    private String telefono;
    private final LocalDateTime fechaRegistro;
    private boolean activo;

    public Usuario(
            String nombre,
            String email,
            String contrasenaHash,
            Rol rol,
            String telefono
    ) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre es obligatorio");
        }
        if (email == null || !email.contains("@")) {
            throw new DomainException("El email es inválido");
        }
        if (contrasenaHash == null || contrasenaHash.isBlank()) {
            throw new DomainException("La contraseña es obligatoria");
        }
        if (rol == null) {
            throw new DomainException("El rol es obligatorio");
        }

        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
        this.telefono = telefono;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

   
    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public void cambiarRol(Rol nuevoRol) {
        if (nuevoRol == null) {
            throw new DomainException("El rol no puede ser nulo");
        }
        this.rol = nuevoRol;
    }

  
    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }
}
