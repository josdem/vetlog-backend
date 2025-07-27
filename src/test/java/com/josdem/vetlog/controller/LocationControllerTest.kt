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
import com.josdem.vetlog.repository.LocationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(LocationController::class)
@TestPropertySource(properties = ["geoToken=testToken"])
class LocationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: LocationRepository

    private val objectMapper = ObjectMapper()

    @Test
    fun `should store location for given pet IDs`() {
        val request = mapOf(
            "latitude" to 35.6895,
            "longitude" to 139.6917,
            "petIds" to listOf(1L, 2L, 3L)
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "testToken")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.CREATED }
        }

        val locations: Map<Long, Location> = repository.findAll()
        assertEquals(3, locations.size)
        assertEquals(35.6895, locations[1L]?.latitude)
        assertEquals(139.6917, locations[2L]?.longitude)
    }

    @Test
    fun `should return bad request when latitude is missing`() {
        val request = mapOf(
            "longitude" to 139.6917,
            "petIds" to listOf(1L, 2L)
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "testToken")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.BAD_REQUEST }
        }
    }

    @Test
    fun `should return bad request when longitude is missing`() {
        val request = mapOf(
            "latitude" to 35.6895,
            "petIds" to listOf(1L, 2L)
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "testToken")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.BAD_REQUEST }
        }
    }

    @Test
    fun `should return bad request when petIds is missing`() {
        val request = mapOf(
            "latitude" to 35.6895,
            "longitude" to 139.6917
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "testToken")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.BAD_REQUEST }
        }
    }

    @Test
    fun `should return bad request when petIds is empty`() {
        val request = mapOf(
            "latitude" to 35.6895,
            "longitude" to 139.6917,
            "petIds" to emptyList<Long>()
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "testToken")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.BAD_REQUEST }
        }
    }

    @Test
    fun `should return forbidden when token is invalid`() {
        val request = mapOf(
            "latitude" to 35.6895,
            "longitude" to 139.6917,
            "petIds" to listOf(1L, 2L, 3L)
        )

        mockMvc.post("/geolocation/storeLocation") {
            contentType = MediaType.APPLICATION_JSON
            header("token", "invalidToken") // 無効なトークン
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.FORBIDDEN }
            jsonPath("$.message") { value("Invalid token") }
        }
    }
}