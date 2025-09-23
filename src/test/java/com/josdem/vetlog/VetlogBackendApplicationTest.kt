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
