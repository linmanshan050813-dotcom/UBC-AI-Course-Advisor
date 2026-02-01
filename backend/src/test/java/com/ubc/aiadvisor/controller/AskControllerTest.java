package com.ubc.aiadvisor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubc.aiadvisor.dto.Citation;
import com.ubc.aiadvisor.dto.QueryRequest;
import com.ubc.aiadvisor.dto.QueryResponse;
import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.AIQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AskController.class)
class AskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIQueryService aiQueryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/ask returns 200 with AI-generated answer and citations")
    void askQuestionSuccess() throws Exception {
        // Arrange
        QueryRequest request = new QueryRequest("What is CPSC 210?");
        String token = "Bearer test-token";
        String userId = "testUser";
        
        Citation citation1 = new Citation("1", "Course description text");
        Citation citation2 = new Citation("2", "Learning objectives text");
        
        QueryResponse response = new QueryResponse("CPSC 210 is a software construction course.", 
                Arrays.asList(citation1, citation2));
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(aiQueryService.processQuery(anyString(), anyString(), isNull())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("CPSC 210 is a software construction course."))
                .andExpect(jsonPath("$.citations").isArray())
                .andExpect(jsonPath("$.citations.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/ask with empty question returns 400")
    void askQuestionEmptyQuestion() throws Exception {
        // Arrange
        QueryRequest request = new QueryRequest("");
        String token = "Bearer test-token";
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn("testUser");

        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Please provide a valid question."));
    }

    @Test
    @DisplayName("POST /api/ask with null question returns 400")
    void askQuestionNullQuestion() throws Exception {
        // Arrange
        QueryRequest request = new QueryRequest(null);
        String token = "Bearer test-token";
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn("testUser");

        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Please provide a valid question."));
    }

    @Test
    @DisplayName("POST /api/ask without auth token returns 401")
    void askQuestionNoAuth() throws Exception {
        // Arrange
        QueryRequest request = new QueryRequest("What is CPSC 210?");
        
        when(jwtUtil.getUserIdFromToken(null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.answer").value("Unauthorized. Please log in."));
    }

    @Test
    @DisplayName("POST /api/ask with invalid token returns 401")
    void askQuestionInvalidToken() throws Exception {
        // Arrange
        QueryRequest request = new QueryRequest("What is CPSC 210?");
        String invalidToken = "Bearer invalid-token";
        
        when(jwtUtil.getUserIdFromToken(invalidToken)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .header("Authorization", invalidToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.answer").value("Unauthorized. Please log in."));
    }
}
