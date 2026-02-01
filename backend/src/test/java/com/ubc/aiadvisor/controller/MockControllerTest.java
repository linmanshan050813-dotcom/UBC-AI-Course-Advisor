package com.ubc.aiadvisor.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockController.class)
class MockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/upload/mock returns mock success response")
    void mockUploadEndpoint() throws Exception {
        mockMvc.perform(get("/api/upload/mock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    @DisplayName("GET /api/query/mock returns mock answer response")
    void mockQueryEndpoint() throws Exception {
        mockMvc.perform(get("/api/query/mock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("mock"));
    }

    @Test
    @DisplayName("GET /api/history/mock returns mock entries array")
    void mockHistoryEndpoint() throws Exception {
        mockMvc.perform(get("/api/history/mock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").isArray());
    }

    @Test
    @DisplayName("Mock endpoints return 200 status code")
    void mockEndpointsReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/upload/mock"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/query/mock"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/history/mock"))
                .andExpect(status().isOk());
    }
}
