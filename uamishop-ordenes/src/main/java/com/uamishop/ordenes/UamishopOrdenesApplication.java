package com.uamishop.ordenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.uamishop.ordenes", "com.uamishop.shared", "com.uamishop.catalogo.api", "com.uamishop.ventas.api"})
@EntityScan(basePackages = {"com.uamishop.ordenes", "com.uamishop.shared"})
@EnableAsync
@org.springframework.scheduling.annotation.EnableScheduling
public class UamishopOrdenesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamishopOrdenesApplication.class, args);
	}

}
