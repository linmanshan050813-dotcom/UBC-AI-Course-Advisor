package com.ubc.aiadvisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS Configuration (DUT)
 * Configures Cross-Origin Resource Sharing for frontend-backend communication.
 * Allows requests from localhost:3000 and localhost:3001 with all HTTP methods.
 * Used throughout all phases of the AI Course Advisor project.
 */
@Configuration
public class CorsConfig {

    /**
     * Create CORS filter bean for cross-origin request handling.
     * 
     * @return CorsFilter configured for frontend origins
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Allow requests from frontend (development and Docker production)
        // In Docker, frontend is served via Nginx on port 3000, so browser requests come from http://localhost:3000
        // Allow all origins for Docker compatibility, or specify exact origins for security
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        // Fallback: also allow exact origins for development
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"));
        
        // Allow all request headers
        config.addAllowedHeader("*");
        
        // Allow all standard HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // Apply CORS configuration to all API paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

