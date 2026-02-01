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
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServiceNotFoundTest {

    @Mock
    AIEngine aiEngine;
    @Mock
    UploadService uploadService;
    @Mock
    SupabaseStorageService supabaseStorageService;

    @InjectMocks
    AIQueryService aiQueryService;

    @Test
    @DisplayName("Unmatched query returns standardized not-found message (R-Q-04)")
    void unmatchedQueryNotFoundMessage() throws Exception {
        String userId = "userNF";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", userId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile("stored.pdf")).thenReturn(File.createTempFile("nff",".pdf"));

        // Simulate AIEngine returning a generic no-data answer without citations
        when(aiEngine.generateAnswer(anyString(), any(File.class)))
                .thenReturn(new AIEngineResult("No relevant info found", new ArrayList<>()));

        QueryResponse response = aiQueryService.processQuery("Tell me about quantum mechanics?", userId, docId);
        assertEquals("Sorry, I could not find grading details in the uploaded syllabus.", response.getAnswer());
    }
}
