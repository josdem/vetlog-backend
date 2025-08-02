package com.josdem.vetlog.aop;

import com.josdem.vetlog.service.impl.DummyService;
import org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.mockito.kotlin.any;
import org.mockito.kotlin.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class ExceptionHandlingAspectTest {

    @Autowired
    private lateinit var dummyService: DummyService

    @SpyBean
    private lateinit var aspect: ExceptionHandlingAspect

    @Test
    fun `should invoke aspect when service throws`() {
        assertThatThrownBy {
            dummyService.alwaysFail()
        }.isInstanceOf(RuntimeException::class.java)

        verify(aspect).handleServiceException(any())
    }
}
