package com.uamishop;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Configuración de OpenAPI / Swagger para practica4
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UAMI Shop API")
                        .version("1.0")
                        .description("API REST para la gestión de uamiShop")
                        .contact(new Contact()
                                .name("Coders")
                                .email("coders@uami.mx"))
                        .license(new License()
                                .name("API License")
                                .url("http://")));
    }
}
