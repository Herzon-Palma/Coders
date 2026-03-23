package com.uamishop.catalogo.listener;

import com.uamishop.catalogo.config.RabbitConfig;
import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.shared.event.ProductoAgregadoAlCarritoEvent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductoAgregadoAlCarritoListener {

    private final ProductoEstadisticasService estadisticasService;

    public ProductoAgregadoAlCarritoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    /**
     * Escucha la cola de RabbitMQ para el evento ProductoAgregadoAlCarritoEvent.
     * Este listener es invocado de forma asíncrona por RabbitMQ cuando el monolito
     * publica un mensaje con la routing key "producto.agregado-carrito".
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_AGREGADO)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductoAgregadoAlCarrito(ProductoAgregadoAlCarritoEvent event) {
        estadisticasService.registrarAgregadoCarrito(event.productoId());
    }
}
