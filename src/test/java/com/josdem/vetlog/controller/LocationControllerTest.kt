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

import com.josdem.vetlog.exception.InvalidTokenException
import com.josdem.vetlog.repository.LocationRepository
import com.josdem.vetlog.command.LocationRequestCommand
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import jakarta.servlet.http.HttpServletResponse

class LocationControllerTest {

    private lateinit var repository: LocationRepository
    private lateinit var response: HttpServletResponse
    private lateinit var controller: LocationController

    @BeforeEach
    fun setUp() {
        repository = mock(LocationRepository::class.java)
        response = mock(HttpServletResponse::class.java)
        controller = LocationController(repository)

        // Set the private fields using reflection
        val domainField = LocationController::class.java.getDeclaredField("domain")
        domainField.isAccessible = true
        domainField.set(controller, "test-domain")

        val tokenField = LocationController::class.java.getDeclaredField("geoToken")
        tokenField.isAccessible = true
        tokenField.set(controller, "testToken")
    }

    @Test
    fun `should throw InvalidTokenException when token is invalid`() {
        // Given
        val command = LocationRequestCommand().apply {
            latitude = 35.6895
            longitude = 139.6917
            petIds = listOf(1L, 2L, 3L)
        }

        // When & Then
        val exception = assertThrows(InvalidTokenException::class.java) {
            controller.storeLocation("invalidToken", command, response)
        }

        assertEquals("Invalid token", exception.message)
    }

    @Test
    fun `should store location successfully when token is valid`() {
        // Given - no need to mock void method, just let it execute
        val command = LocationRequestCommand().apply {
            latitude = 35.6895
            longitude = 139.6917
            petIds = listOf(1L, 2L, 3L)
        }

        // When
        val result = controller.storeLocation("testToken", command, response)

        // Then
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals("Location stored successfully", result.body)

        // Verify that save was called 3 times (once for each petId)
        verify(repository, times(3)).save(anyLong(), any())
    }
}