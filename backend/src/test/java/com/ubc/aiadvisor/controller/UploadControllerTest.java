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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UploadController.class)
class UploadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadService uploadService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("UploadController returns 200 with success response when authenticated")
    void uploadReturnsOk() throws Exception {
        // Arrange
        String token = "Bearer test-token";
        String userId = "testUser";
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("success", true);
        resp.put("filename", "uploads/abc123.pdf");
        resp.put("docId", "doc123");
        
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(uploadService.isValidFileType(anyString())).thenReturn(true);
        when(uploadService.isValidFileSize(anyLong(), anyLong())).thenReturn(true);
        when(uploadService.processUpload(any(), eq(userId))).thenReturn(resp);

        // Act & Assert
        mockMvc.perform(multipart("/api/upload")
                .file(new org.springframework.mock.web.MockMultipartFile("file","syllabus.pdf","application/pdf",new byte[]{1,2}))
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.filename").value("uploads/abc123.pdf"));
    }

    @Test
    @DisplayName("UploadController rejects request without auth token with 401")
    void uploadUnauthorized() throws Exception {
        // Arrange
        when(jwtUtil.getUserIdFromToken(null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(multipart("/api/upload")
                .file(new org.springframework.mock.web.MockMultipartFile("file","syllabus.pdf","application/pdf",new byte[]{1,2}))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("UploadController rejects invalid file type with 400")
    void uploadInvalidType() throws Exception {
        // Arrange
        String token = "Bearer test-token";
        when(jwtUtil.getUserIdFromToken(token)).thenReturn("testUser");
        when(uploadService.isValidFileType(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(multipart("/api/upload")
                .file(new org.springframework.mock.web.MockMultipartFile("file","notes.png","image/png",new byte[]{1}))
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("UploadController rejects oversize file with 413")
    void uploadOversize() throws Exception {
        // Arrange
        String token = "Bearer test-token";
        when(jwtUtil.getUserIdFromToken(token)).thenReturn("testUser");
        when(uploadService.isValidFileType(anyString())).thenReturn(true);
        when(uploadService.isValidFileSize(anyLong(), anyLong())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(multipart("/api/upload")
                .file(new org.springframework.mock.web.MockMultipartFile("file","big.pdf","application/pdf",new byte[1024]))
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isPayloadTooLarge());
    }
}
