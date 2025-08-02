package com.josdem.vetlog

import com.josdem.vetlog.configuration.SwaggerConfig
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SwaggerConfigTest {

    private lateinit var swaggerConfig: SwaggerConfig

    @BeforeEach
    fun setUp() {
        swaggerConfig = SwaggerConfig()
    }

    @Test
    @DisplayName("Should create OpenAPI object with correct metadata")
    fun shouldCreateOpenAPIWithCorrectMetadata(testInfo: TestInfo) {
        log.info(testInfo.displayName)

        val openAPI = swaggerConfig.vetlogOpenAPI()

        Assertions.assertEquals("Vetlog API", openAPI.info.title, "API title should be 'Vetlog API'")
        Assertions.assertEquals("API documentation for Vetlog backend", openAPI.info.description)
        Assertions.assertEquals("1.0", openAPI.info.version)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SwaggerConfigTest::class.java)
    }
}
