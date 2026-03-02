package com.uamishop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API REST UAMIShop")
                                                .version("1.0")
                                                .description("Ejemplo de API del curso TSIS - Módulo Órdenes")
                                                .contact(new Contact()
                                                                .name("Coders")
                                                                .url("www.miurl.com")
                                                                .email("yo@xanum.uam.mx"))
                                                .license(new License()
                                                                .name("API License")
                                                                .url("API license URL")));
        }
}
