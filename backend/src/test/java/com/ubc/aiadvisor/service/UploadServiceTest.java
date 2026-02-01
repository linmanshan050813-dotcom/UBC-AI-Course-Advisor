package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import com.ubc.aiadvisor.repository.UploadedDocRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @Mock
    private UploadedDocRepository uploadedDocRepository;

    @Mock
    private AIEngine aiEngine;

    @InjectMocks
    private UploadService uploadService;

    @Test
    @DisplayName("processUpload saves file to Supabase and returns success (R-U-01)")
    void processUploadSuccess() throws IOException {
        // Arrange
        String userId = "testUser";
        MockMultipartFile file = new MockMultipartFile("file", "syllabus.pdf", "application/pdf", new byte[]{1,2,3});
        String supabaseFilename = "uploads/abc123.pdf";
        File tempFile = new File("temp.pdf");
        String courseName = "CPEN 221: Software Construction";
        
        when(supabaseStorageService.uploadFile(any())).thenReturn(supabaseFilename);
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(tempFile);
        when(aiEngine.extractCourseName(any(File.class))).thenReturn(courseName);
        when(uploadedDocRepository.save(any(UploadedDoc.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Map<String, Object> result = uploadService.processUpload(file, userId);

        // Assert
        assertEquals(true, result.get("success"));
        assertEquals(supabaseFilename, result.get("filename"));
        assertEquals("syllabus.pdf", result.get("originalFilename"));
        assertEquals(courseName, result.get("courseName"));
        verify(supabaseStorageService).uploadFile(file);
        verify(uploadedDocRepository).save(any(UploadedDoc.class));
    }

    @Test
    @DisplayName("isValidFileType allows .pdf, .txt, .docx; rejects others (R-U-02)")
    void validFileTypeChecks() {
        // Assert
        assertTrue(uploadService.isValidFileType("doc.PDF"));
        assertTrue(uploadService.isValidFileType("doc.pdf"));
        assertTrue(uploadService.isValidFileType("notes.txt"));
        assertTrue(uploadService.isValidFileType("document.docx"));
        assertFalse(uploadService.isValidFileType("image.png"));
        assertFalse(uploadService.isValidFileType("script.js"));
        assertFalse(uploadService.isValidFileType(null));
    }

    @Test
    @DisplayName("isValidFileSize enforces MB limit (R-U-03)")
    void validFileSizeChecks() {
        // Assert
        // 5 MB limit
        assertTrue(uploadService.isValidFileSize(5L * 1024 * 1024, 5));
        assertFalse(uploadService.isValidFileSize(6L * 1024 * 1024, 5));
        assertTrue(uploadService.isValidFileSize(0, 5));
        assertTrue(uploadService.isValidFileSize(1024, 5)); // 1 KB
    }
}
