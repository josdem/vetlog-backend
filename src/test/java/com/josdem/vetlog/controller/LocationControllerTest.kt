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

package com.josdem.vetlog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.josdem.vetlog.command.LocationRequestCommand
import com.josdem.vetlog.repository.LocationRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@WebMvcTest(LocationController::class)  // ← CHANGE BACK TO @WebMvcTest
@TestPropertySource(properties = ["geoToken=testToken", "app.domain=testdomain.com"])
@SpringBootTest
class LocationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Suppress("DEPRECATION")
    @MockBean
    private lateinit var locationRepository: LocationRepository

    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun `should store location successfully with valid token`(testInfo: TestInfo) {
        log.info(testInfo.displayName)

        val locationCommand = LocationRequestCommand().apply {
            latitude = 40.7128
            longitude = -74.0060
            petIds = listOf(1L, 2L)
        }

        mockMvc
            .perform(
                post("/geolocation/storeLocation")
                    .header("token", "testToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(locationCommand))
            )
            .andExpect(status().isCreated())
            .andExpect(content().string("Location stored successfully"))
    }

    @Test
    fun `should return forbidden with invalid token`(testInfo: TestInfo) {
        log.info(testInfo.displayName)

        val locationCommand = LocationRequestCommand().apply {
            latitude = 40.7128
            longitude = -74.0060
            petIds = listOf(3L, 4L)
        }

        mockMvc
            .perform(
                post("/geolocation/storeLocation")
                    .header("token", "invalidToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(locationCommand))
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value("Invalid token"))
    }
}