package com.uamishop; // Debe coincidir con la carpeta src/main/java/com/uamishop

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TiendaUamiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TiendaUamiApplication.class, args);
    }
}

