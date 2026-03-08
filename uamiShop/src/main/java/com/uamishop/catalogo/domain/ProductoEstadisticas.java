package com.uamishop.catalogo.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_estadisticas")
public class ProductoEstadisticas {
    @Id
    @Column(columnDefinition = "VARBINARY(16) ")
    private UUID productoEId;
    private long ventastotales;
    private long cantidadVendida;
    private long vecesAgregadoCarrito;
    private long ultimaVentaAt;
    private long ultimaAgregadoCarritoAt;

    public ProductoEstadisticas() {
    }

    public ProductoEstadisticas(UUID productoEId) {
        this.productoEId = productoEId;
        this.ventastotales = 0;
        this.cantidadVendida = 0;
        this.vecesAgregadoCarrito = 0;
        this.ultimaVentaAt = 0;
        this.ultimaAgregadoCarritoAt = 0;
    }

    public UUID getProductoEId() {
        return productoEId;
    }

    public long getVentastotales() {
        return ventastotales;
    }

    public long getCantidadVendida() {
        return cantidadVendida;
    }

    public long getVecesAgregadoCarrito() {
        return vecesAgregadoCarrito;
    }

    public long getUltimaVentaAt() {
        return ultimaVentaAt;
    }

    public long getUltimaAgregadoCarritoAt() {
        return ultimaAgregadoCarritoAt;
    }
    // Setters
    public void setProductoEId(UUID productoEId) {
        this.productoEId = productoEId;
    }

    public void setVentastotales(long ventastotales) {
        this.ventastotales = ventastotales;
    }

    public void setCantidadVendida(long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public void setVecesAgregadoCarrito(long vecesAgregadoCarrito) {
        this.vecesAgregadoCarrito = vecesAgregadoCarrito;
    }

    public void setUltimaVentaAt(long ultimaVentaAt) {
        this.ultimaVentaAt = ultimaVentaAt;
    }

    public void setUltimaAgregadoCarritoAt(long ultimaAgregadoCarritoAt) {
        this.ultimaAgregadoCarritoAt = ultimaAgregadoCarritoAt;
    }

    


}
