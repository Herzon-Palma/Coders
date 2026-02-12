package com.uamishop.catalogo.domain;

import com.uamishop.shared.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    private Categoriaid mockCategoriaId = Categoriaid.generar();

    @Test
    @DisplayName("RN-CAT-01: No debe permitir nombres menores a 3 caracteres")
    void nombreDemasiadoCorto() {
        assertThrows(RuntimeException.class, () -> {
            Producto.crear("Ab", "Descripción válida", Money.pesos(100), mockCategoriaId);
        });
    }

    @Test
    @DisplayName("RN-CAT-05: No debe permitir incrementos de precio mayores al 50%")
    void incrementoPrecioExcesivo() {
        Producto producto = Producto.crear("Laptop Gaming", "Core i9", Money.pesos(1000), mockCategoriaId);
        Money precioMuyCaro = Money.pesos(1501); // 50.1% de incremento
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            producto.cambiarPrecio(precioMuyCaro);
        });
        assertEquals("El incremento no puede superar el 50%", exception.getMessage());
    }

    @Test
    @DisplayName("RN-CAT-09: No debe permitir activar producto sin imágenes")
    void activarSinImagenes() {
        Producto producto = Producto.crear("Smartphone", "Gama alta", Money.pesos(500), mockCategoriaId);
        
        assertThrows(RuntimeException.class, producto::activar);
    }

    @Test
    @DisplayName("RN-CAT-07: Validar formato de URL de imagen")
    void urlImagenInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Imagen("ftp://servidor.com/foto.jpg", "Foto", 1);
        });
    }

    @Test
    @DisplayName("Prueba de Flujo Exitoso: Crear, agregar imagen y activar")
    void flujoExitoso() {
        Producto producto = Producto.crear("Monitor 4K", "32 pulgadas", Money.pesos(300), mockCategoriaId);
        Imagen img = new Imagen("https://uami.mx/foto.png", "Vista frontal", 1);
        
        producto.agregarImagen(img);
        producto.activar();
        
        // Si no lanza excepción, la lógica es correcta
        assertDoesNotThrow(producto::activar);
    }
}
