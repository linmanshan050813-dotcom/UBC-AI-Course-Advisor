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
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServiceConcurrencyTest {

    @InjectMocks
    AIQueryService aiQueryService;

    @Mock
    AIEngine aiEngine;

    @Mock
    UploadService uploadService;

    @Mock
    SupabaseStorageService supabaseStorageService;

    @Test
    @DisplayName("processQuery handles 20 concurrent requests")
    void processQueryConcurrent20() throws Exception {
        String userId = "user-concurrent";
        UploadedDoc doc = new UploadedDoc("file.pdf", "orig.pdf", userId);
        when(uploadService.getUploadById(anyString())).thenReturn(doc);
        when(uploadService.getUserUploads(userId)).thenReturn(List.of(doc));
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("dummy",".pdf"));

        AIEngineResult engineResult = new AIEngineResult("Concurrent answer", List.of("Citation A", "Citation B"));
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenReturn(engineResult);

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<QueryResponse>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            String q = "What is policy #" + i + "?";
            futures.add(executor.submit(() -> aiQueryService.processQuery(q, userId, doc.getId())));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not finish in time");

        for (Future<QueryResponse> f : futures) {
            QueryResponse r = f.get(3, TimeUnit.SECONDS);
            assertNotNull(r);
            assertTrue(r.getAnswer().contains("Concurrent"));
            assertFalse(r.getCitations().isEmpty());
        }
    }
}
