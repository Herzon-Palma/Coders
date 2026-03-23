package com.uamishop.ventas.listener;

import com.uamishop.ventas.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uamishop.shared.event.OrdenCreadaEvent;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.service.CarritoService;

@Component
public class OrdenCreadaListener {
    private final CarritoService carritoService;

    public OrdenCreadaListener(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_VENTAS_ORDEN_CREADA)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrdenCreada(OrdenCreadaEvent event) {
        carritoService.completarCheckout(new CarritoId(event.carritoId()));
    }
}
