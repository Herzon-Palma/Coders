package com.uamishop.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.uamishop.catalogo", "com.uamishop.shared"})
@EntityScan(basePackages = {"com.uamishop.catalogo", "com.uamishop.shared"})
public class UamishopCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamishopCatalogoApplication.class, args);
	}

}
