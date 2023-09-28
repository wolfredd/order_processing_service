package com.sagatrading.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Replace with the actual origins you want to allow
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Replace with the HTTP methods you want to allow
                .allowedHeaders("*");// Replace with the actual headers you want to allow
    }
}