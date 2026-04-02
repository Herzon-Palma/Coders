package com.uamishop.ordenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication(scanBasePackages = {"com.uamishop.ordenes", "com.uamishop.shared"})
@EnableJpaRepositories(basePackages = "com.uamishop.ordenes.repository")
@EntityScan(basePackages = {"com.uamishop.ordenes.domain", "com.uamishop.shared.domain"})
@EnableRabbit
@EnableAsync
public class UamishopOrdenesApplication {
    public static void main(String[] args) {
        SpringApplication.run(UamishopOrdenesApplication.class, args);
    }
}
