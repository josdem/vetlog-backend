package com.josdem.vetlog.aop

import com.josdem.vetlog.exception.InvalidTokenException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.slf4j.LoggerFactory

internal class ExceptionHandlingAspectTest {

    @Mock
    private lateinit var invalidTokenException: InvalidTokenException

    @InjectMocks
    private val exceptionHandlingAspect = ExceptionHandlingAspect()

    private val log = LoggerFactory.getLogger(this::class.java)

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should handle exception`() {
        exceptionHandlingAspect.handleServiceException(invalidTokenException)
        verify(invalidTokenException).message
    }
}