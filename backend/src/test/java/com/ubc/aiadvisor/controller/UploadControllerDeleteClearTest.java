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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadController.class)
class UploadControllerDeleteClearTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UploadService uploadService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    @DisplayName("DELETE /api/upload/{id} deletes upload for authorized user")
    void deleteUploadAuthorized() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("userDel");
        doNothing().when(uploadService).deleteUpload(anyString(), anyString());

        mockMvc.perform(delete("/api/upload/abc123")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/upload/{id} unauthorized without token")
    void deleteUploadUnauthorized() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(null);
        mockMvc.perform(delete("/api/upload/abc123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/uploads clears all uploads for authorized user")
    void clearUploadsAuthorized() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("userClear");
        doNothing().when(uploadService).clearUploads(anyString());

        mockMvc.perform(delete("/api/uploads")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
