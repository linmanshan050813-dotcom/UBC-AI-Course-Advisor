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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceTest {

    @Mock
    private AIEngine aiEngine;

    @Mock
    private UploadService uploadService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private AIQueryService aiQueryService;

    @Test
    @DisplayName("processQuery returns answer with citations for valid question")
    void processQuerySuccess() throws Exception {
        // Arrange
        String question = "What is CPSC 210?";
        String userId = "testUser";
        String docId = "doc123";
        
        UploadedDoc doc = new UploadedDoc("uploads/syllabus.pdf", "syllabus.pdf", userId);
        File pdfFile = new File("test.pdf");
        
        AIEngineResult aiResult = new AIEngineResult(
            "CPSC 210 is about software construction.",
            Arrays.asList("Course description (Page 1, Top)", "Learning objectives (Page 2, Middle)")
        );
        
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(pdfFile);
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenReturn(aiResult);

        // Act
        QueryResponse response = aiQueryService.processQuery(question, userId, docId);

        // Assert
        assertNotNull(response);
        assertEquals("CPSC 210 is about software construction.", response.getAnswer());
        assertEquals(2, response.getCitations().size());
    }

    @Test
    @DisplayName("processQuery returns error message for empty question")
    void processQueryEmptyQuestion() {
        // Act
        QueryResponse response = aiQueryService.processQuery("", "testUser", null);

        // Assert
        assertNotNull(response);
        assertEquals("Please ask questions related to the course", response.getAnswer());
        assertTrue(response.getCitations().isEmpty());
    }

    @Test
    @DisplayName("processQuery returns error message for null question")
    void processQueryNullQuestion() {
        // Act
        QueryResponse response = aiQueryService.processQuery(null, "testUser", null);

        // Assert
        assertNotNull(response);
        assertEquals("Please ask questions related to the course", response.getAnswer());
        assertTrue(response.getCitations().isEmpty());
    }

    @Test
    @DisplayName("processQuery returns error when no document found")
    void processQueryNoDocument() {
        // Arrange
        String userId = "testUser";
        when(uploadService.getUserUploads(userId)).thenReturn(Collections.emptyList());

        // Act
        QueryResponse response = aiQueryService.processQuery("Question", userId, null);

        // Assert
        assertNotNull(response);
        assertEquals("No course document found. Please upload a syllabus first.", response.getAnswer());
        assertTrue(response.getCitations().isEmpty());
    }

    @Test
    @DisplayName("processQuery returns unauthorized for wrong user")
    void processQueryUnauthorizedAccess() {
        // Arrange
        String userId = "testUser";
        String docId = "doc123";
        UploadedDoc doc = new UploadedDoc("uploads/syllabus.pdf", "syllabus.pdf", "otherUser");
        
        when(uploadService.getUploadById(docId)).thenReturn(doc);

        // Act
        QueryResponse response = aiQueryService.processQuery("Question", userId, docId);

        // Assert
        assertNotNull(response);
        assertEquals("Unauthorized access to document.", response.getAnswer());
        assertTrue(response.getCitations().isEmpty());
    }

    @Test
    @DisplayName("processQuery handles AI engine exceptions gracefully")
    void processQueryAIException() throws Exception {
        // Arrange
        String userId = "testUser";
        UploadedDoc doc = new UploadedDoc("uploads/syllabus.pdf", "syllabus.pdf", userId);
        File pdfFile = new File("test.pdf");
        
        when(uploadService.getUserUploads(userId)).thenReturn(Arrays.asList(doc));
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(pdfFile);
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenThrow(new RuntimeException("AI service error"));

        // Act
        QueryResponse response = aiQueryService.processQuery("Question", userId, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.getAnswer().contains("error"));
        assertTrue(response.getCitations().isEmpty());
    }
}
