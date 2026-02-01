package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceSourceHighlightTest {

    @Mock AIEngine aiEngine;
    @Mock UploadService uploadService;
    @Mock SupabaseStorageService supabaseStorageService;
    @InjectMocks AIQueryService aiQueryService;

    @Test
    @DisplayName("Source segment includes highlight marker (R-C-01/R-C-02)")
    void sourceSegmentContainsHighlight() throws IOException {
        UploadedDoc doc = new UploadedDoc("stored.pdf","original.pdf","userSH");
        when(uploadService.getUploadById("docSH")).thenReturn(doc);
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("srcHL",".pdf"));
        String segment = aiQueryService.getSourceSegment("userSH","docSH","cit123");
        assertTrue(segment.contains("[HIGHLIGHT]"));
        assertTrue(segment.contains("cit123"));
    }
}
