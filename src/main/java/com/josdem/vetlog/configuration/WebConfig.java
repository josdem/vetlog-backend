package com.josdem.vetlog.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestInterceptor myRequestInterceptor;

    @Autowired
    public WebConfig(RequestInterceptor myRequestInterceptor) {
        this.myRequestInterceptor = myRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myRequestInterceptor).addPathPatterns("/geolocation/**"); // Apply to paths starting with /api/
    }
}
