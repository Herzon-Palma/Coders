package com.uamishop.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.uamishop.ventas", "com.uamishop.shared"})
@EntityScan(basePackages = {"com.uamishop.ventas", "com.uamishop.shared"})
@EnableAsync
public class UamishopVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamishopVentasApplication.class, args);
	}

}
