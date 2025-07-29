package com.josdem.vetlog.exception

import com.josdem.vetlog.dto.ErrorDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

class GlobalExceptionHandlerTest {

    private val globalExceptionHandler = GlobalExceptionHandler()

    @Test
    fun `should handle InvalidTokenException and return 403`() {
        val exception = InvalidTokenException("Invalid token")
        val request = ServletWebRequest(MockHttpServletRequest())

        val response = globalExceptionHandler.handleInvalidTokenException(exception, request)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        val errorDto = response.body as ErrorDto
        assertEquals("Invalid token", errorDto.message)
        assertEquals(403, errorDto.status)
    }

    @Test
    fun `should handle generic Exception and return 500`() {
        val exception = RuntimeException("Something went wrong")
        val request = ServletWebRequest(MockHttpServletRequest())

        val response = globalExceptionHandler.handleGenericException(exception, request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        val errorDto = response.body as ErrorDto
        assertEquals("Internal server error", errorDto.message)
        assertEquals(500, errorDto.status)
    }
}