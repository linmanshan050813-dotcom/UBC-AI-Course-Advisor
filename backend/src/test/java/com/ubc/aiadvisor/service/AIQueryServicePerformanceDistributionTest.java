package com.ubc.aiadvisor.service;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServicePerformanceDistributionTest {

    @Mock
    private AIEngine aiEngine;
    @Mock
    private UploadService uploadService;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private AIQueryService aiQueryService;

    @Test
    @DisplayName("95th percentile latency < 3000ms over 30 queries (R-Q-05/R-N-04)")
    void percentileUnderThreshold() throws Exception {
        String userId = "userPerf";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", userId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile("stored.pdf")).thenReturn(File.createTempFile("distPerf",".pdf"));

        when(aiEngine.generateAnswer(anyString(), any(File.class)))
                .thenAnswer(invocation -> new AIEngineResult("Answer", List.of("Citation")));

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Instant start = Instant.now();
            aiQueryService.processQuery("What is policy?", userId, docId);
            long ms = Duration.between(start, Instant.now()).toMillis();
            durations.add(ms);
            assertTrue(ms < 3000, "Individual query exceeded threshold");
        }

        Collections.sort(durations);
        int idx95 = (int) Math.ceil(0.95 * durations.size()) - 1;
        long p95 = durations.get(idx95);
        assertTrue(p95 < 3000, "95th percentile " + p95 + "ms exceeds 3000ms threshold");
    }
}
