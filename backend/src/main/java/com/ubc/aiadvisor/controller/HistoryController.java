package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.dto.HistoryItem;
import com.ubc.aiadvisor.dto.HistoryRequest;
import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * History Controller (DUT)
 * Handles requests for retrieving and adding Q/A history entries.
 * Used in Phase 3 of the AI Course Advisor project.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class HistoryController {

    private final HistoryService historyService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HistoryController(HistoryService historyService, JwtUtil jwtUtil) {
        this.historyService = historyService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Retrieve the last 10 history entries for the authenticated user.
     * GET /api/history
     * 
     * @param token Authorization header with JWT
     * @return list of history item DTOs
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryItem>> getHistory(@RequestHeader(value = "Authorization", required = false) String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<HistoryEntry> history = historyService.getUserHistory(userId);
        
        // Convert Entity to DTO
        List<HistoryItem> response = history.stream().map(entry -> {
            HistoryItem item = new HistoryItem();
            item.setId(entry.getId());
            item.setQuestion(entry.getQuestion());
            item.setAnswer(entry.getAnswer());
            item.setTimestamp(entry.getTimestampAsString());
            
            try {
                if (entry.getCitations() != null && !entry.getCitations().isEmpty()) {
                    List<String> citations = objectMapper.readValue(entry.getCitations(), new TypeReference<List<String>>(){});
                    item.setCitations(citations);
                } else {
                    item.setCitations(new ArrayList<>());
                }
            } catch (Exception e) {
                item.setCitations(new ArrayList<>());
            }
            
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific history entry.
     * DELETE /api/history/{id}
     * 
     * @param id ID of the history entry to delete
     * @param token Authorization header with JWT
     * @return 200 OK on success
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistoryEntry(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        historyService.deleteHistoryEntry(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear all history entries for the authenticated user.
     * DELETE /api/history
     * 
     * @param token Authorization header with JWT
     * @return 200 OK on success
     */
    @DeleteMapping("/history")
    public ResponseEntity<?> clearHistory(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        historyService.clearHistory(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Add a new history entry for the authenticated user.
     * POST /api/history
     * 
     * @param request the history request containing question and answer
     * @param token Authorization header with JWT
     * @return created history entry or error response
     */
    @PostMapping("/history")
    public ResponseEntity<?> addHistory(
            @RequestBody HistoryRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            // System.err.println("HISTORY ERROR: No valid user ID from token");
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized - please log in again");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        try {
            // Convert citations list to JSON string for storage
            String citationsJson = null;
            if (request.getCitations() != null) {
                citationsJson = objectMapper.writeValueAsString(request.getCitations());
            }

            HistoryEntry entry = historyService.addEntry(
                request.getQuestion(), 
                request.getAnswer(), 
                citationsJson, 
                userId
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (IllegalArgumentException e) {
            // System.err.println("HISTORY ERROR: Invalid arguments - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("HISTORY ERROR: Failed to save - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to save history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
