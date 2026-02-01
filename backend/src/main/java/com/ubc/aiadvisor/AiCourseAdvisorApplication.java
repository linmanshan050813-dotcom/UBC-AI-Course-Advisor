package com.ubc.aiadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * AI Course Advisor Application (DUT)
 * Main Spring Boot application entry point.
 * Auto-scans and configures all components in sub-packages.
 * Used throughout all phases of the AI Course Advisor project.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AiCourseAdvisorApplication {

    /**
     * Application entry point.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AiCourseAdvisorApplication.class, args);
    }
}

