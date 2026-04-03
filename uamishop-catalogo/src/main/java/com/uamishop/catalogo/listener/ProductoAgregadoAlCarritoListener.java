package com.uamishop.catalogo.listener;

import com.uamishop.catalogo.config.RabbitConfig;
import com.uamishop.catalogo.repository.EventoProcesado;
import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.shared.event.ProductoAgregadoAlCarritoEvent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductoAgregadoAlCarritoListener {

    private final EventoProcesado eventoProcesado;
    private final ProductoEstadisticasService estadisticasService;

    public ProductoAgregadoAlCarritoListener(ProductoEstadisticasService estadisticasService, EventoProcesado eventoProcesado) {
        this.estadisticasService = estadisticasService;
        this.eventoProcesado = eventoProcesado;
    }

    /**
     * Escucha la cola de RabbitMQ para el evento ProductoAgregadoAlCarritoEvent.
     * Este listener es invocado de forma asíncrona por RabbitMQ cuando el monolito
     * publica un mensaje con la routing key "producto.agregado-carrito".
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_AGREGADO)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductoAgregadoAlCarrito(ProductoAgregadoAlCarritoEvent event) {

        if(eventoProcesado.existsById(event.eventoId())) {
            return; // Evento ya procesado, ignorar
        }

        estadisticasService.registrarAgregadoCarrito(event.productoId());
        // Marcar el evento como procesado
        eventoProcesado.save(new com.uamishop.catalogo.domain.Eventoprocesado(event.eventoId(), "ProductoAgregadoAlCarritoListener"));
    }
}
