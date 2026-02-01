package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceMissingFileTest {

    @Mock
    AIEngine aiEngine;
    @Mock
    UploadService uploadService;
    @Mock
    SupabaseStorageService supabaseStorageService;

    @InjectMocks
    AIQueryService aiQueryService;

    @Test
    @DisplayName("Missing original file returns R-C-03 message")
    void missingFileReturnsMessage() throws IOException {
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", "userA");
        when(uploadService.getUploadById("doc1")).thenReturn(doc);
        when(supabaseStorageService.downloadFile(anyString())).thenThrow(new IOException("404"));

        String segment = aiQueryService.getSourceSegment("userA", "doc1", "cit-123");
        assertEquals("Original file not found.", segment);
    }

    @Test
    @DisplayName("Unauthorized access to segment")
    void unauthorizedSegmentAccess() {
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", "userA");
        when(uploadService.getUploadById("doc1")).thenReturn(doc);

        String segment = aiQueryService.getSourceSegment("otherUser", "doc1", "cit-999");
        assertEquals("Unauthorized access to document.", segment);
    }
}
