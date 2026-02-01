package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.dto.QueryResponse;
import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServiceIndexSearchTest {

    @Mock
    private AIEngine aiEngine;
    @Mock
    private UploadService uploadService;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private AIQueryService aiQueryService;

    @Test
    @DisplayName("Index search returns non-empty citations (R-U-05)")
    void indexSearchReturnsCitations() throws Exception {
        String userId = "userIdx";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", userId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile("stored.pdf")).thenReturn(File.createTempFile("idx",".pdf"));
        when(aiEngine.generateAnswer(anyString(), any(File.class)))
                .thenAnswer(invocation -> new AIEngineResult("Indexed answer", List.of("Quote (Page 1, Top)")));

        QueryResponse response = aiQueryService.processQuery("Find any policy", userId, docId);
        assertNotNull(response.getCitations());
        assertFalse(response.getCitations().isEmpty(), "Expected at least one citation from index search");
    }
}
