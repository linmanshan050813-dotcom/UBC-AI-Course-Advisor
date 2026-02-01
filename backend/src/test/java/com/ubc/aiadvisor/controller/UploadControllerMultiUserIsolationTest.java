package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UploadController.class)
class UploadControllerMultiUserIsolationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UploadService uploadService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    @DisplayName("User cannot delete another user's upload (R-W-01)")
    void cannotDeleteOthersUpload() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("userA");
        when(jwtUtil.getUserRoleFromToken(anyString())).thenReturn("professor");
        // Simulate UploadService rejecting deletion due to ownership mismatch
        doThrow(new IllegalArgumentException("Document not found or unauthorized"))
                .when(uploadService).deleteUpload(anyString(), anyString());

        mockMvc.perform(delete("/api/upload/foreignDoc")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden: cannot modify another user's document"));
    }
}
