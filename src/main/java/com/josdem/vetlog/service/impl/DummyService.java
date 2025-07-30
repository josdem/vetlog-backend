package com.josdem.vetlog.service.impl;

import org.springframework.stereotype.Service;

@Service
public class DummyService {
    
    public void alwaysFail() {
        throw new RuntimeException("Dummy exception for aspect testing");
    }
}
