package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UploadController.class)
class UploadControllerRoleAccessTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UploadService uploadService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    @DisplayName("Student role upload blocked with 403")
    void studentCannotUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "syllabus.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy".getBytes());

        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("studentUser");
        when(jwtUtil.getUserRoleFromToken(anyString())).thenReturn("student");

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden: professor role required"));
    }

    @Test
    @DisplayName("Professor role upload succeeds")
    void professorUploadSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "syllabus.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy".getBytes());

        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("profUser");
        when(jwtUtil.getUserRoleFromToken(anyString())).thenReturn("professor");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("docId", "doc-1");
        when(uploadService.processUpload(any(), anyString())).thenReturn(result);
        when(uploadService.isValidFileType(anyString())).thenCallRealMethod(); // uses real logic
        when(uploadService.isValidFileSize(any(Long.class), any(Long.class))).thenCallRealMethod();

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student role delete blocked with 403")
    void studentCannotDelete() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("studentUser");
        when(jwtUtil.getUserRoleFromToken(anyString())).thenReturn("student");

        mockMvc.perform(delete("/api/upload/abc123")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden: professor role required"));
    }

    @Test
    @DisplayName("Professor role delete succeeds")
    void professorDeleteSucceeds() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("profUser");
        when(jwtUtil.getUserRoleFromToken(anyString())).thenReturn("professor");
        // deleteUpload void method, no exception required
        mockMvc.perform(delete("/api/upload/abc123")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
