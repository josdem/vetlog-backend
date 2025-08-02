package com.josdem.vetlog;
import com.josdem.vetlog.configuration.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;
    private static final Logger log = LoggerFactory.getLogger(SwaggerConfigTest.class);

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI object with correct metadata")
    void shouldCreateOpenAPIWithCorrectMetadata(TestInfo testInfo) {
        log.info(testInfo.getDisplayName());

        OpenAPI openAPI = swaggerConfig.vetlogOpenAPI();

        assertEquals("Vetlog API", openAPI.getInfo().getTitle(), "API title should be 'Vetlog API'");
        assertEquals("API documentation for Vetlog backend", openAPI.getInfo().getDescription());
        assertEquals("1.0", openAPI.getInfo().getVersion());
    }
}
