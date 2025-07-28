package com.josdem.vetlog.exception




import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

class GlobalExceptionHandlerTest {

    private val globalExceptionHandler = GlobalExceptionHandler()

    @Test
    fun `should handle InvalidTokenException and return forbidden status`() {
        // Given
        val exception = InvalidTokenException("Invalid token")
        val request = ServletWebRequest(MockHttpServletRequest())

        // When
        val response = globalExceptionHandler.handleInvalidTokenException(exception, request)

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(403, response.body?.status)
        assertEquals("Invalid token", response.body?.message)
    }
}