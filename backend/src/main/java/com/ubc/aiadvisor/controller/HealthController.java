package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.service.AIEngine;
import com.ubc.aiadvisor.service.AIQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller (DUT)
 * Provides health check endpoint for service monitoring.
 * Returns fixed success response without dependencies.
 * Used in Phase 0-4 of the AI Course Advisor project.
 */
@RestController
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"},
    methods = {RequestMethod.GET, RequestMethod.OPTIONS},
    allowedHeaders = "*"
)
public class HealthController {

    private final AIQueryService aiQueryService;
    private final AIEngine aiEngine;

    @Autowired(required = false)
    public HealthController(AIQueryService aiQueryService, AIEngine aiEngine) {
        this.aiQueryService = aiQueryService;
        this.aiEngine = aiEngine;
    }

    /**
     * Health check endpoint that always returns success status.
     * No dependencies required - pure status indicator.
     * GET /health
     * 
     * @return ResponseEntity with HTTP 200 and JSON containing status
     */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> health() {
        // Build response map with status information
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        
        // Return 200 OK with explicit JSON content type
        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    /**
     * Performance metrics endpoint for monitoring AI engine performance.
     * Returns statistics about query processing, timeouts, and fallbacks.
     * GET /api/metrics
     * 
     * @return ResponseEntity with HTTP 200 and JSON containing performance metrics
     */
    @GetMapping(value = "/api/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> metrics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get AI Engine performance metrics
            if (aiEngine != null) {
                AIEngine.PerformanceMetrics engineMetrics = aiEngine.getPerformanceMetrics();
                Map<String, Object> engineStats = new HashMap<>();
                engineStats.put("totalRequests", engineMetrics.getTotalRequests());
                engineStats.put("successCount", engineMetrics.getSuccessCount());
                engineStats.put("errorCount", engineMetrics.getErrorCount());
                engineStats.put("successRate", String.format("%.2f%%", engineMetrics.getSuccessRate() * 100));
                response.put("aiEngine", engineStats);
            }
            
            response.put("status", "ok");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve metrics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
