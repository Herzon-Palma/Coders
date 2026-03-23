package com.uamishop.catalogo.listener;

import com.uamishop.catalogo.config.RabbitConfig;
import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.shared.event.ProductoAgregadoAlCarritoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductoAgregadoAlCarritoListener {
    private final ProductoEstadisticasService estadisticasService;

    public ProductoAgregadoAlCarritoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_AGREGADO)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductoAgregadoAlCarrito(ProductoAgregadoAlCarritoEvent event) {
        estadisticasService.registrarAgregadoCarrito(event.productoId());
    }
}
