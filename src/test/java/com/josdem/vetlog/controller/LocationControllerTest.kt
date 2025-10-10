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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 👈 important
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["geoToken=testToken", "app.domain=testdomain.com"])
class LocationControllerTest
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val locationRepository: LocationRepository
) {

  private val log = LoggerFactory.getLogger(this::class.java)
  private val testInstanceName = "test-hazelcast"

  @BeforeEach
  fun cleanUp() {
    locationRepository.deleteAll()
  }

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
                .content(objectMapper.writeValueAsString(locationCommand)))
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
                .content(objectMapper.writeValueAsString(locationCommand)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.message").value("Invalid token"))
  }

  @Test
  fun `should store pets ids and relative locations`(testInfo: TestInfo) {
    val petLocationCommand = PetLocationCommand(listOf(1L, 2L, 3L))
    mockMvc
        .perform(
            post("/geolocation/storePetLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand)))
        .andExpect(status().isCreated())

    assertEquals(3, locationRepository.findAll().size)
  }

  @Test
  fun `should find pet location by id`() {
    // First, save a test location
    val petLocationCommand = PetLocationCommand(listOf(11L))
    mockMvc
        .perform(
            post("/geolocation/storePetLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand)))
        .andExpect(status().isCreated())
    val savedLocation: Location? = locationRepository.findByPetId(11L)
    assertNotNull(savedLocation, "Location should be saved and retrievable")

    mockMvc
        .perform(get("/geolocation/storeLocation/11").header("token", "testToken"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.latitude").value(savedLocation?.latitude))
        .andExpect(jsonPath("$.longitude").value(savedLocation?.longitude))
  }
  // --- Negative Test Cases ---

  @Test
  fun `should return not found for non-existing pet id`() {
    mockMvc
        .perform(get("/geolocation/storeLocation/999").header("token", "testToken"))
        .andExpect(status().isNotFound)
  }

  @Test
  fun `should return bad request when missing latitude in storeLocation`() {
    val invalidCommand = mapOf("longitude" to -74.0060, "petIds" to listOf(1L, 2L))
    val result =
        mockMvc
            .perform(
                post("/geolocation/storeLocation")
                    .header("token", "testToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
            .andExpect(status().isBadRequest)
            .andReturn()
    log.error("Missing latitude response: ${result.response.contentAsString}")
  }

  @Test
  fun `should return bad request when missing petIds in storeLocation`() {
    val invalidCommand = mapOf("latitude" to 40.7128, "longitude" to -74.0060)
    val result =
        mockMvc
            .perform(
                post("/geolocation/storeLocation")
                    .header("token", "testToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
            .andExpect(status().isBadRequest)
            .andReturn()
    log.error("Missing petIds response: ${result.response.contentAsString}")
  }

  @Test
  fun `should return bad request when missing petsIds in storePetLocation`() {
    val invalidCommand = mapOf<String, Any>()
    val result =
        mockMvc
            .perform(
                post("/geolocation/storePetLocation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
            .andExpect(status().isBadRequest)
            .andReturn()
    log.error("Missing petsIds response: ${result.response.contentAsString}")
  }

  @Test
  fun `should delete all locations successfully`() {
    // Pre-populate repository
    locationRepository.save(1L, Location(10.0, 20.0))
    locationRepository.save(2L, Location(30.0, 40.0))

    val result =
        mockMvc
            .perform(delete("/geolocation/removeAll").header("token", "testToken"))
            .andExpect(status().isOk)
            .andExpect(content().string("Deleted all pet's locations"))
            .andReturn()

    assertEquals(0, locationRepository.findAll().size)
  }

  @Test
  fun `should delete locations for given pet ids`() {
    locationRepository.save(1L, Location(10.0, 20.0))
    locationRepository.save(2L, Location(30.0, 40.0))
    locationRepository.save(3L, Location(50.0, 60.0))

    val petLocationCommand = mapOf("petsIds" to listOf(1L, 3L))
    val result =
        mockMvc
            .perform(
                delete("/geolocation/storeLocation")
                    .header("token", "testToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(petLocationCommand)))
            .andExpect(status().isNoContent)
            .andExpect(content().string("Deleted locations for pets: [1, 3]"))
            .andReturn()

    assertEquals(1, locationRepository.findAll().size)
    assertNotNull(locationRepository.findByPetId(2L))
  }

  @Test
  fun `should return bad request when deleting locations with empty pet ids`() {
    val invalidCommand = mapOf<String, Any>()
    val result =
        mockMvc
            .perform(
                delete("/geolocation/storeLocation")
                    .header("token", "testToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
            .andExpect(status().isBadRequest)
            .andReturn()
    log.error("Delete locations with empty pet ids response: ${result.response.contentAsString}")
  }

  @Test
  fun `remove all should fail when token is missing`() {
    mockMvc
        .perform(delete("/geolocation/removeAll"))
        .andExpect(status().isForbidden) // or whatever your code throws
  }

  @Test
  fun `remove all should fail when invalid token`() {
    mockMvc
        .perform(delete("/geolocation/removeAll").header("token", "invalidToken"))
        .andExpect(status().isForbidden) // or whatever your code throws
  }

  @Test
  fun `remove by id should fail when token is missing`() {
    val petLocationCommand = mapOf("petsIds" to listOf(1L, 3L))

    mockMvc
        .perform(
            delete("/geolocation/storeLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand)))
        .andExpect(status().isForbidden) // or whatever your code throws
  }

  @Test
  fun `remove by id should fail when invalid token`() {
    val petLocationCommand = mapOf("petsIds" to listOf(1L, 3L))

    mockMvc
        .perform(
            delete("/geolocation/storeLocation")
                .header("token", "invalidToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petLocationCommand)))
        .andExpect(status().isForbidden) // or whatever your code throws
  }
}
