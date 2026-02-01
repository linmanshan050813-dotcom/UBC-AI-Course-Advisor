package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import com.ubc.aiadvisor.repository.UploadedDocRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UploadServiceEdgeCaseTest tests additional edge cases and error scenarios
 * for the UploadService that weren't covered in the basic tests.
 */
@ExtendWith(MockitoExtension.class)
class UploadServiceEdgeCaseTest {

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @Mock
    private UploadedDocRepository uploadedDocRepository;

    @Mock
    private AIEngine aiEngine;

    @InjectMocks
    private UploadService uploadService;

    @Test
    @DisplayName("isValidFileType handles null filename")
    void isValidFileTypeNullFilename() {
        // Act
        boolean result = uploadService.isValidFileType(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidFileType handles empty string")
    void isValidFileTypeEmptyString() {
        // Act
        boolean result = uploadService.isValidFileType("");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidFileType handles filename without extension")
    void isValidFileTypeNoExtension() {
        // Act
        boolean result = uploadService.isValidFileType("document");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidFileType handles filename with multiple dots")
    void isValidFileTypeMultipleDots() {
        // Act
        boolean result = uploadService.isValidFileType("my.file.name.pdf");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidFileType handles uppercase extensions")
    void isValidFileTypeUppercaseExtensions() {
        // Act & Assert
        assertTrue(uploadService.isValidFileType("document.PDF"));
        assertTrue(uploadService.isValidFileType("document.TXT"));
        assertTrue(uploadService.isValidFileType("document.DOCX"));
    }

    @Test
    @DisplayName("isValidFileType handles mixed case extensions")
    void isValidFileTypeMixedCase() {
        // Act & Assert
        assertTrue(uploadService.isValidFileType("document.Pdf"));
        assertTrue(uploadService.isValidFileType("document.TxT"));
        assertTrue(uploadService.isValidFileType("document.DocX"));
    }

    @Test
    @DisplayName("isValidFileType rejects invalid extensions")
    void isValidFileTypeInvalidExtensions() {
        // Act & Assert
        assertFalse(uploadService.isValidFileType("document.exe"));
        assertFalse(uploadService.isValidFileType("document.jpg"));
        assertFalse(uploadService.isValidFileType("document.zip"));
        assertFalse(uploadService.isValidFileType("document.html"));
    }

    @Test
    @DisplayName("isValidFileSize handles zero size")
    void isValidFileSizeZeroSize() {
        // Act
        boolean result = uploadService.isValidFileSize(0, 5);

        // Assert
        assertTrue(result); // Zero size is technically valid (empty file)
    }

    @Test
    @DisplayName("isValidFileSize handles negative size")
    void isValidFileSizeNegativeSize() {
        // Act
        boolean result = uploadService.isValidFileSize(-100, 5);

        // Assert
        // Note: Current implementation allows negative sizes (mathematical comparison)
        // In practice, file system APIs won't return negative sizes
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidFileSize handles exact limit")
    void isValidFileSizeExactLimit() {
        // Arrange
        long fiveMB = 5L * 1024 * 1024;

        // Act
        boolean result = uploadService.isValidFileSize(fiveMB, 5);

        // Assert
        assertTrue(result); // Exact limit should be valid
    }

    @Test
    @DisplayName("isValidFileSize handles one byte over limit")
    void isValidFileSizeOneByteOver() {
        // Arrange
        long fiveMBPlusOne = 5L * 1024 * 1024 + 1;

        // Act
        boolean result = uploadService.isValidFileSize(fiveMBPlusOne, 5);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("getUserUploads returns empty list for user with no uploads")
    void getUserUploadsNoUploads() {
        // Arrange
        String userId = "user-no-uploads";
        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(userId)).thenReturn(new ArrayList<>());

        // Act
        List<UploadedDoc> result = uploadService.getUserUploads(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(uploadedDocRepository).findByUserIdOrderByUploadedAtDesc(userId);
    }

    @Test
    @DisplayName("getUserUploads returns multiple uploads for user")
    void getUserUploadsMultiple() {
        // Arrange
        String userId = "user-multi";
        List<UploadedDoc> uploads = Arrays.asList(
                new UploadedDoc("file1.pdf", "original1.pdf", userId),
                new UploadedDoc("file2.pdf", "original2.pdf", userId),
                new UploadedDoc("file3.pdf", "original3.pdf", userId)
        );
        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(userId)).thenReturn(uploads);

        // Act
        List<UploadedDoc> result = uploadService.getUserUploads(userId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(uploadedDocRepository).findByUserIdOrderByUploadedAtDesc(userId);
    }

    @Test
    @DisplayName("getUploadById returns doc when found")
    void getUploadByIdFound() {
        // Arrange
        String docId = "doc123";
        UploadedDoc doc = new UploadedDoc("file.pdf", "original.pdf", "user1");
        when(uploadedDocRepository.findById(docId)).thenReturn(Optional.of(doc));

        // Act
        UploadedDoc result = uploadService.getUploadById(docId);

        // Assert
        assertNotNull(result);
        assertEquals(doc, result);
        verify(uploadedDocRepository).findById(docId);
    }

    @Test
    @DisplayName("getUploadById returns null when not found")
    void getUploadByIdNotFound() {
        // Arrange
        String docId = "nonexistent";
        when(uploadedDocRepository.findById(docId)).thenReturn(Optional.empty());

        // Act
        UploadedDoc result = uploadService.getUploadById(docId);

        // Assert
        assertNull(result);
        verify(uploadedDocRepository).findById(docId);
    }

    @Test
    @DisplayName("getUserUploads handles null userId gracefully")
    void getUserUploadsNullUserId() {
        // Arrange
        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(null)).thenReturn(new ArrayList<>());

        // Act
        List<UploadedDoc> result = uploadService.getUserUploads(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getUploadById handles null docId gracefully")
    void getUploadByIdNullDocId() {
        // Arrange
        when(uploadedDocRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        UploadedDoc result = uploadService.getUploadById(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("isValidFileSize handles very large limit")
    void isValidFileSizeVeryLargeLimit() {
        // Arrange
        long twoGB = 2L * 1024 * 1024 * 1024;

        // Act
        boolean result = uploadService.isValidFileSize(twoGB, 3000); // 3000 MB limit

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidFileType handles filename starting with dot")
    void isValidFileTypeStartsWithDot() {
        // Act
        boolean result = uploadService.isValidFileType(".pdf");

        // Assert
        assertTrue(result); // Hidden file with valid extension
    }

    @Test
    @DisplayName("isValidFileType handles filename ending with dot")
    void isValidFileTypeEndsWithDot() {
        // Act
        boolean result = uploadService.isValidFileType("document.pdf.");

        // Assert
        assertFalse(result); // Invalid format
    }
}
