package com.josdem.vetlog.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI vetlogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vetlog API")
                        .description("API documentation for Vetlog backend")
                        .version("1.0"));

    }
}
