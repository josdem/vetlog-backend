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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.assertNotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
internal class VetlogBackendApplicationTest {

  @Autowired private lateinit var applicationContext: ApplicationContext

  private val log = LoggerFactory.getLogger(this::class.java)

  @Test
  fun `should load application`(testInfo: TestInfo) {
    log.info(testInfo.displayName)
    VetlogBackendApplication.main(arrayOf())
    assertNotNull(
        applicationContext, "The application context should have been initialized and not be null")
  }
}
