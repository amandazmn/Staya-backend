package com.staya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stayaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Staya API")
                        .description("API de gestão hoteleira: clientes, quartos, reservas, check-in/check-out e painel da recepção.")
                        .version("0.0.1"));
    }
}
