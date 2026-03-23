package com.uamishop.catalogo;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.uamishop.catalogo", "com.uamishop.shared"})
@EntityScan(basePackages = {"com.uamishop.catalogo", "com.uamishop.shared"})
@EnableRabbit
@EnableAsync
public class UamishopCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamishopCatalogoApplication.class, args);
	}

}
