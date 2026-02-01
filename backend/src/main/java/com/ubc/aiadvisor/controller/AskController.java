package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.dto.QueryRequest;
import com.ubc.aiadvisor.dto.QueryResponse;
import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.AIQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Ask Controller (DUT)
 * Handles AI Q&A requests.
 * Routes user questions to AIQueryService and returns generated answers.
 * Used in Phase 4 of the AI Course Advisor project.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class AskController {

    private final AIQueryService aiQueryService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AskController(AIQueryService aiQueryService, JwtUtil jwtUtil) {
        this.aiQueryService = aiQueryService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Process user question and return AI answer.
     * POST /api/ask
     * 
     * @param request the query request containing the question
     * @param token Authorization header with JWT
     * @return response entity with answer and citations
     */
    @PostMapping("/ask")
    public ResponseEntity<QueryResponse> askQuestion(
            @RequestBody QueryRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new QueryResponse("Unauthorized. Please log in.", new ArrayList<>()));
        }

        if (request == null || request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new QueryResponse("Please provide a valid question.", new ArrayList<>()));
        }

        String docId = request.getDocId();

        QueryResponse response = aiQueryService.processQuery(request.getQuestion(), userId, docId);
        return ResponseEntity.ok(response);
    }
}
