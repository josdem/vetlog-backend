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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["geoToken=testToken", "app.domain=testdomain.com"])
class LocationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var locationRepository: LocationRepository

    private val log = LoggerFactory.getLogger(this::class.java)

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
      locationRepository = LocationRepository()
      locationRepository.save(1L,Location(0.0,0.0))
      locationRepository.save(2L,Location(0.0,0.0))
      locationRepository.save(3L,Location(0.0,0.0))
  
      mockMvc
        .perform(
          post("/geolocation/storePetLocation")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(petLocationCommand))
      ).andExpect(status().isCreated())
      
      assertEquals(3,locationRepository.findAll().size)
    }
}