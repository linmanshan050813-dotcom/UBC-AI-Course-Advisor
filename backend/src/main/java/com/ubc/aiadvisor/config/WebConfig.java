package com.ubc.aiadvisor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration (DUT)
 * Additional web MVC configuration for CORS and request handling.
 * Complements CorsConfig with additional CORS mappings.
 * Used throughout all phases of the AI Course Advisor project.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Add CORS mappings for all API endpoints.
     * 
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
            .allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);  // Cache preflight requests for 1 hour
    }
}

