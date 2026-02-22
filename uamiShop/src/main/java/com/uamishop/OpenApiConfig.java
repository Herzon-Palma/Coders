package com.uamishop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("UamiShop API REST")
                                                .version("1.0")
                                                .description(
                                                                "API REST de la tienda UamiShop - Proyecto de Temas Selectos de Ingeniería de Software")
                                                .contact(new Contact()
                                                                .name("Equipo Coders")
                                                                .url("https://github.com/coders")
                                                                .email("coders@uam.mx")));
        }
}
