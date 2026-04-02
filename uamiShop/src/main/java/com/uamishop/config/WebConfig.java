package com.uamishop.config;

import com.uamishop.shared.domain.Productoid;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.UUID;

/**
 * Configuración de conversores para que Spring pueda deserializar
 * los Value Objects de dominio a partir de Strings en @PathVariable.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(new StringToProductoidConverter());
    }



    private static class StringToProductoidConverter implements Converter<String, Productoid> {
        @Override
        public Productoid convert(String source) {
            return new Productoid(UUID.fromString(source));
        }
    }
}
