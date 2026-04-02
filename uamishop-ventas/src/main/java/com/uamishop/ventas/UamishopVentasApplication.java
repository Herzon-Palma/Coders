package com.uamishop.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.uamishop.ventas.repository")
@EntityScan(basePackages = {"com.uamishop.ventas.domain", "com.uamishop.shared.domain"})
@EnableRabbit
@EnableAsync // Para los listeners
public class UamishopVentasApplication {
    public static void main(String[] args) {
        SpringApplication.run(UamishopVentasApplication.class, args);
    }
}
