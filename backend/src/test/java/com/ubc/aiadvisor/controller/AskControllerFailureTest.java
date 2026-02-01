package com.ubc.aiadvisor.controller;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AskController.class)
class AskControllerFailureTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIQueryService aiQueryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("AskController returns fallback response gracefully when AI unavailable")
    void askReturnsFallbackGracefully() throws Exception {
        // Arrange
        String token = "Bearer test-token";
        String userId = "testUser";
        QueryResponse fallback = new QueryResponse("An error occurred while processing your question. Please try again.", 
                new java.util.ArrayList<>());
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(aiQueryService.processQuery(anyString(), anyString(), isNull())).thenReturn(fallback);

        String body = "{\"question\":\"Describe grading\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/ask")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("An error occurred while processing your question. Please try again."));
    }
}
