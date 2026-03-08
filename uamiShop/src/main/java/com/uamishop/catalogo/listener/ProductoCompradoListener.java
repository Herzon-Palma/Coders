package com.uamishop.catalogo.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.shared.event.ProductoCompradoEvent;



@Component
public class ProductoCompradoListener {
    private final ProductoEstadisticasService estadisticasService;
    
    public ProductoCompradoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Asegura que se ejecute en una nueva transacción independiente
    public void onProductoComprado(ProductoCompradoEvent event) {
        event.productos().forEach(item -> {
            estadisticasService.registrarVenta(item.productoId(), item.cantidad());
        });
    }
}
