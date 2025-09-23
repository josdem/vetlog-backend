/*
  Copyright 2025 Jose Morales contact@josdem.io

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

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
