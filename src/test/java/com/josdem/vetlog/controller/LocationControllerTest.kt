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
import com.josdem.vetlog.model.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.concurrent.ConcurrentHashMap

@WebMvcTest(LocationController::class)
class LocationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var controller: LocationController

    private val objectMapper = ObjectMapper()

    @Test
    fun `should store location for given pet IDs`() {
        // Arrange
        val testToken = "test-token"
        val request = mapOf(
            "latitude" to 35.6895,
            "longitude" to 139.6917,
            "petIds" to listOf(1L, 2L, 3L)
        )

        System.setProperty("geoToken", testToken)

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", testToken)
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }

        val locations: ConcurrentHashMap<Long, Location> = controller.petLocations
        assertEquals(3, locations.size)
        assertEquals(35.6895, locations[1L]?.lat)
        assertEquals(139.6917, locations[2L]?.lng)
    }
}
