package com.uamishop.ordenes.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "uamishop.events";
    public static final String QUEUE_ORDEN_CREADA = "ventas.orden.creada"; // El queue usado en Ventas
    public static final String RK_PRODUCTO_COMPRADO = "catalogo.producto.comprado";
    public static final String RK_ORDEN_CREADA = "ventas.orden.creada";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue ordenCreadaQueue() {
        return new Queue(QUEUE_ORDEN_CREADA, true);
    }

    @Bean
    public Binding bindingOrdenCreada(Queue ordenCreadaQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(ordenCreadaQueue).to(eventsExchange).with(RK_ORDEN_CREADA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar Instant
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
