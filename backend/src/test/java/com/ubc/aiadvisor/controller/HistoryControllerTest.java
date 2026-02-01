package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.HistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubc.aiadvisor.dto.HistoryRequest;
import org.springframework.http.MediaType;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryService historyService;

    @MockBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/history returns history entries for authenticated user")
    void historyReturnsOk() throws Exception {
        // Arrange
        String token = "Bearer test-token";
        String userId = "testUser";
        
        List<HistoryEntry> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HistoryEntry e = new HistoryEntry("Q"+i, "A"+i, "[\"cit1\", \"cit2\"]", userId);
            e.setCreatedAt(java.time.LocalDateTime.now().minusMinutes(i));
            entries.add(e);
        }
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(historyService.getUserHistory(userId)).thenReturn(entries);

        // Act & Assert
        mockMvc.perform(get("/api/history")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].question").value("Q0"))
                .andExpect(jsonPath("$[0].answer").value("A0"))
                .andExpect(jsonPath("$[0].citations[0]").value("cit1"));
    }

    @Test
    @DisplayName("GET /api/history returns 401 without auth token")
    void historyUnauthorized() throws Exception {
        // Arrange
        when(jwtUtil.getUserIdFromToken(null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/history adds new entry")
    void addHistoryReturnsCreated() throws Exception {
        String token = "Bearer test-token";
        String userId = "testUser";
        HistoryRequest request = new HistoryRequest();
        request.setQuestion("Question");
        request.setAnswer("Answer");
        request.setCitations(List.of("citation1"));

        HistoryEntry entry = new HistoryEntry("Question", "Answer", "[\"citation1\"]", userId);

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(historyService.addEntry(anyString(), anyString(), anyString(), anyString())).thenReturn(entry);

        mockMvc.perform(post("/api/history")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("Question"));
    }

    @Test
    @DisplayName("POST /api/history returns 401 when unauthorized")
    void addHistoryUnauthorized() throws Exception {
        HistoryRequest request = new HistoryRequest();
        request.setQuestion("Question");
        request.setAnswer("Answer");

        when(jwtUtil.getUserIdFromToken(any())).thenReturn(null);

        mockMvc.perform(post("/api/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/history returns 400 on IllegalArgumentException")
    void addHistoryBadRequest() throws Exception {
        String token = "Bearer test-token";
        String userId = "testUser";
        HistoryRequest request = new HistoryRequest();
        request.setQuestion("Question");

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(historyService.addEntry(any(), any(), any(), any())).thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/history")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid data"));
    }

    @Test
    @DisplayName("POST /api/history returns 500 on generic Exception")
    void addHistoryInternalServerError() throws Exception {
        String token = "Bearer test-token";
        String userId = "testUser";
        HistoryRequest request = new HistoryRequest();
        request.setQuestion("Question");

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(historyService.addEntry(any(), any(), any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/history")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to save history: Database error"));
    }

    @Test
    @DisplayName("DELETE /api/history/{id} deletes entry")
    void deleteHistoryEntrySuccess() throws Exception {
        String token = "Bearer test-token";
        String userId = "testUser";
        String entryId = "entry1";

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);

        mockMvc.perform(delete("/api/history/{id}", entryId)
                .header("Authorization", token))
                .andExpect(status().isOk());

        verify(historyService).deleteHistoryEntry(entryId, userId);
    }

    @Test
    @DisplayName("DELETE /api/history/{id} returns 401 unauthorized")
    void deleteHistoryEntryUnauthorized() throws Exception {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(null);

        mockMvc.perform(delete("/api/history/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/history clears all entries")
    void clearHistorySuccess() throws Exception {
        String token = "Bearer test-token";
        String userId = "testUser";

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);

        mockMvc.perform(delete("/api/history")
                .header("Authorization", token))
                .andExpect(status().isOk());

        verify(historyService).clearHistory(userId);
    }

    @Test
    @DisplayName("DELETE /api/history returns 401 unauthorized")
    void clearHistoryUnauthorized() throws Exception {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(null);

        mockMvc.perform(delete("/api/history"))
                .andExpect(status().isUnauthorized());
    }
}
