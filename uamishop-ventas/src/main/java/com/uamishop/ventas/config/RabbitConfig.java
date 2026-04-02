package com.uamishop.ventas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "uamishop.events";

    // Ventas publica que se agregó al carrito
    public static final String RK_PRODUCTO_AGREGADO = "producto.agregado-carrito";

    // Ventas escucha que se creó la orden
    public static final String QUEUE_VENTAS_ORDEN_CREADA = "ventas.orden.creada";
    public static final String RK_ORDEN_CREADA = "orden.creada";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue ventasOrdenCreadaQueue() {
        return new Queue(QUEUE_VENTAS_ORDEN_CREADA, true);
    }

    @Bean
    public Binding ventasOrdenCreadaBinding(Queue ventasOrdenCreadaQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(ventasOrdenCreadaQueue)
                .to(eventsExchange)
                .with(RK_ORDEN_CREADA);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
