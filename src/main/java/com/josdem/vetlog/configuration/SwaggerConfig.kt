package com.josdem.vetlog.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun vetlogOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Vetlog API")
                    .description("API documentation for Vetlog backend")
                    .version("1.0")
            )
    }
}
