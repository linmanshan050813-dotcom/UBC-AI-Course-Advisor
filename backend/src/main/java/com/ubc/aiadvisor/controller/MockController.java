package com.ubc.aiadvisor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock API Controller (DUT)
 * Provides mock endpoints for testing frontend integration.
 * Returns fixed responses without business logic.
 * Used in Phase 0 of the AI Course Advisor project.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"})
public class MockController {

    /**
     * Mock upload endpoint for testing.
     * GET /api/upload/mock
     * 
     * @return ResponseEntity with mock success response
     */
    @GetMapping("/upload/mock")
    public ResponseEntity<Map<String, Object>> mockUpload() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Mock query endpoint for testing.
     * GET /api/query/mock
     * 
     * @return ResponseEntity with mock answer response
     */
    @GetMapping("/query/mock")
    public ResponseEntity<Map<String, Object>> mockQuery() {
        Map<String, Object> response = new HashMap<>();
        response.put("answer", "mock");
        return ResponseEntity.ok(response);
    }

    /**
     * Mock history endpoint for testing.
     * GET /api/history/mock
     * 
     * @return ResponseEntity with mock empty history entries
     */
    @GetMapping("/history/mock")
    public ResponseEntity<Map<String, Object>> mockHistory() {
        Map<String, Object> response = new HashMap<>();
        response.put("entries", new Object[0]);
        return ResponseEntity.ok(response);
    }
}

