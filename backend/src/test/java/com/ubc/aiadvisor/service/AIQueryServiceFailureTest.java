package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.dto.QueryResponse;
import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceFailureTest {

    @Mock
    private AIEngine aiEngine;

    @Mock
    private UploadService uploadService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private AIQueryService aiQueryService;

    @Test
    @DisplayName("processQuery handles AIEngine exception gracefully and returns error message (R-Q-04)")
    void processQueryHandlesException() throws Exception {
        // Arrange
        String userId = "testUser";
        UploadedDoc doc = new UploadedDoc("uploads/syllabus.pdf", "syllabus.pdf", userId);
        File pdfFile = new File("test.pdf");
        
        when(uploadService.getUserUploads(userId)).thenReturn(Arrays.asList(doc));
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(pdfFile);
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenThrow(new RuntimeException("AI service failure"));

        // Act
        QueryResponse response = aiQueryService.processQuery("Explain grading breakdown", userId, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.getAnswer().toLowerCase().contains("error"));
        assertTrue(response.getCitations().isEmpty());
    }
}
