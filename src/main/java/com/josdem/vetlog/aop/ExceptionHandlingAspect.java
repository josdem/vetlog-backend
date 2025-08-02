package com.josdem.vetlog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.AfterThrowing;

@Slf4j
@Aspect
@Component
public class ExceptionHandlingAspect {
    
    /*
      For catching every exception thrown inside the service layer
      (single recovery point for all service errors).
    */

    @AfterThrowing(
        pointcut = "execution(* com.josdem.vetlog.service..*(..))", 
        throwing = "ex"
    )
    public void handleServiceException(Throwable ex) {
        log.error("Unhandled exception in service layer: {}", ex.getMessage());
    }
}
