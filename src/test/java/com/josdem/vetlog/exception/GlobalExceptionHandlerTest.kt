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

    @Test
    fun `should handle MethodArgumentNotValidException and return 400`() {
        val bindingResult = org.mockito.Mockito.mock(org.springframework.validation.BindingResult::class.java)
        val objectError = org.springframework.validation.ObjectError("object", "Validation failed")
        org.mockito.Mockito.`when`(bindingResult.allErrors).thenReturn(listOf(objectError))
        val exception = org.springframework.web.bind.MethodArgumentNotValidException(
            org.springframework.core.MethodParameter(
                Any::class.java.getMethod("toString"),
                -1
            ),
            bindingResult
        )

        val response = globalExceptionHandler.handleValidationExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorDto = response.body as ErrorDto
        assertEquals(400, errorDto.status)
        assertEquals("Validation failed", errorDto.message)
    }

    @Test
    fun `should return only first validation error in MethodArgumentNotValidException`() {
        val bindingResult = org.mockito.Mockito.mock(org.springframework.validation.BindingResult::class.java)
        val error1 = org.springframework.validation.ObjectError("object", "First error")
        val error2 = org.springframework.validation.ObjectError("object", "Second error")
        org.mockito.Mockito.`when`(bindingResult.allErrors).thenReturn(listOf(error1, error2))
        val exception = org.springframework.web.bind.MethodArgumentNotValidException(
            org.springframework.core.MethodParameter(
                Any::class.java.getMethod("toString"),
                -1
            ),
            bindingResult
        )

        val response = globalExceptionHandler.handleValidationExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorDto = response.body as ErrorDto
        assertEquals(400, errorDto.status)
        assertEquals("First error", errorDto.message)
    }
}