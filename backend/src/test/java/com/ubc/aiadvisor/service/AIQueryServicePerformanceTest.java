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
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServicePerformanceTest {

    @InjectMocks
    AIQueryService aiQueryService;

    @Mock
    AIEngine aiEngine;

    @Mock
    UploadService uploadService;

    @Mock
    SupabaseStorageService supabaseStorageService;

    @Test
    @DisplayName("processQuery completes under 3 seconds (mock mode)")
    void processQueryUnderThreeSeconds() throws Exception {
        String userId = "perf-user";
        UploadedDoc doc = new UploadedDoc("perf.pdf", "orig.pdf", userId);
        when(uploadService.getUploadById(anyString())).thenReturn(doc);
        when(uploadService.getUserUploads(userId)).thenReturn(List.of(doc));
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("perf",".pdf"));
        when(aiEngine.generateAnswer(anyString(), any(File.class)))
                .thenReturn(new AIEngineResult("Performance answer", List.of("Cite")));

        Instant start = Instant.now();
        QueryResponse response = aiQueryService.processQuery("What is grading policy?", userId, doc.getId());
        Instant end = Instant.now();

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("Performance"));
        Duration duration = Duration.between(start, end);
        assertTrue(duration.toMillis() < 3000, "Query exceeded 3 second threshold: " + duration.toMillis());
    }
}
