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
import com.hazelcast.core.Hazelcast
import com.josdem.vetlog.command.LocationRequestCommand
import com.josdem.vetlog.command.PetLocationCommand
import com.josdem.vetlog.model.Location
import com.josdem.vetlog.repository.LocationRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 👈 important
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["geoToken=testToken", "app.domain=testdomain.com"])
class LocationControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val locationRepository: LocationRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val testInstanceName = "test-hazelcast"

    @AfterAll
     fun tearDown() {
        // Shutdown only the test Hazelcast instance
        Hazelcast.getHazelcastInstanceByName(testInstanceName)?.shutdown()
    }

    @Test
    fun `should store location successfully with valid token`(testInfo: TestInfo) {
        log.info(testInfo.displayName)

        val locationCommand = LocationRequestCommand(40.7128, -74.0060, listOf(1L, 2L))

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

        val locationCommand = LocationRequestCommand(40.7128, -74.0060, listOf(3L, 2L))

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

    @Test 
    fun `should store pets ids and relative locations`(testInfo: TestInfo){
      val petLocationCommand = PetLocationCommand(listOf(1L, 2L, 3L))
        mockMvc.perform(
            post("/geolocation/storePetLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand))
        )
            .andExpect(status().isCreated())

      
      assertEquals(3,locationRepository.findAll().size)
    }
    @Test
    fun `should find pet location by id`() {
        // First, save a test location
        val petLocationCommand = PetLocationCommand(listOf(11L))
        mockMvc.perform(
            post("/geolocation/storePetLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand))
        )
            .andExpect(status().isCreated)
        val savedLocation: Location? = locationRepository.findByPetId(11L)
        assertTrue(savedLocation != null, "Location should be saved and retrievable")
    }

}