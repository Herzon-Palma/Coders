package com.uamishop.ventas.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.uamishop.shared.event.OrdenCreadaEvent;
import com.uamishop.ventas.config.RabbitConfig;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.repository.EventoProcesadoRepository;
import com.uamishop.ventas.service.CarritoService;

@Component
public class OrdenCreadaListener {
    private final CarritoService carritoService;
    private final EventoProcesadoRepository eventoProcesadoRepository;

    public OrdenCreadaListener(CarritoService carritoService, EventoProcesadoRepository eventoProcesadoRepository) {
        this.carritoService = carritoService;
        this.eventoProcesadoRepository = eventoProcesadoRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_VENTAS_ORDEN_CREADA)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrdenCreada(OrdenCreadaEvent event) {
        // Verificar si el evento ya fue procesado
        if (eventoProcesadoRepository.existsById(event.eventoId())) {
            return; // Evento ya procesado, ignorar
        }

        carritoService.completarCheckout(new CarritoId(event.carritoId()));

        // Marcar el evento como procesado
        eventoProcesadoRepository.save(new com.uamishop.ventas.domain.Eventoprocesado(event.eventoId(), "OrdenCreadaListener"));
    }
}
