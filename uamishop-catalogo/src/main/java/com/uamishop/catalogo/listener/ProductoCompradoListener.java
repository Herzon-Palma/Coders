package com.uamishop.catalogo.listener;

import com.uamishop.catalogo.config.RabbitConfig;
import com.uamishop.catalogo.domain.Eventoprocesado;
import com.uamishop.catalogo.repository.EventoProcesado;
import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.shared.event.ProductoCompradoEvent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductoCompradoListener {

    private final ProductoEstadisticasService estadisticasService;
    private final EventoProcesado eventoProcesadoRepository;

    public ProductoCompradoListener(ProductoEstadisticasService estadisticasService, EventoProcesado eventoProcesadoRepository) {
        this.estadisticasService = estadisticasService;
        this.eventoProcesadoRepository = eventoProcesadoRepository;
    }

    /**
     * Escucha la cola de RabbitMQ para el evento ProductoCompradoEvent.
     * Este listener es invocado de forma asíncrona por RabbitMQ cuando el monolito
     * completa la creación de una orden, publicando un mensaje con routing key
     * "producto.comprado".
     * Por cada ítem de la orden, actualiza las estadísticas de ventas del catálogo.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_COMPRADO)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductoComprado(ProductoCompradoEvent event) {
        
        if (eventoProcesadoRepository.existsById(event.eventoId())) {
            return; // Evento ya procesado, ignorar
        }
        
        event.productos().forEach(item ->
            estadisticasService.registrarVenta(item.productoId(), item.cantidad())
        );
        // Marcar el evento como procesado
        eventoProcesadoRepository.save(new com.uamishop.catalogo.domain.Eventoprocesado(event.eventoId(), "ProductoCompradoListener"));
    }
}
