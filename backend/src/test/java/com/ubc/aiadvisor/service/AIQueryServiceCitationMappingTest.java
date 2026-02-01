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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceCitationMappingTest {

    @Mock AIEngine aiEngine;
    @Mock UploadService uploadService;
    @Mock SupabaseStorageService supabaseStorageService;
    @InjectMocks AIQueryService aiQueryService;

    @Test
    @DisplayName("JSON citations mapped to QueryResponse objects (R-Q-02)")
    void jsonCitationsMapped() throws Exception {
        String userId = "userCM";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf","original.pdf",userId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile("stored.pdf")).thenReturn(File.createTempFile("cmap",".pdf"));

        AIEngineResult engineResult = new AIEngineResult("Answer summary", List.of("Quote1 (Page 1, Top)", "Quote2 (Page 2, Middle)"));
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenReturn(engineResult);

        QueryResponse response = aiQueryService.processQuery("Late policy?", userId, docId);
        assertEquals("Answer summary", response.getAnswer());
        assertFalse(response.getCitations().isEmpty());
        assertEquals(2, response.getCitations().size());
    }
}
