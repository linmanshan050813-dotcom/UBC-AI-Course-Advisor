package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.dto.QueryResponse;
import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AIQueryServiceOffTopicTest {

    @Mock
    private AIEngine aiEngine;

    @Mock
    private UploadService uploadService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private AIQueryService aiQueryService;

    @Test
    void offTopicQuestionReturnsRequiredMessage() throws Exception {
        String userId = "user-123";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf", "original.pdf", userId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile("stored.pdf")).thenReturn(File.createTempFile("dummyOffTopic", ".pdf"));

        // Mock AIEngine to return off-topic response
        when(aiEngine.generateAnswer(Mockito.eq("What is your favorite movie?"), Mockito.any(File.class)))
                .thenReturn(new AIEngineResult("Please ask questions related to the course", new ArrayList<>()));

        Instant start = Instant.now();
        QueryResponse response = aiQueryService.processQuery("What is your favorite movie?", userId, docId);
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();

        assertEquals("Please ask questions related to the course", response.getAnswer(), "Off-topic message mismatch");
        assertTrue(response.getCitations().isEmpty(), "Citations should be empty for off-topic question");
        assertTrue(elapsedMs < 3000, "Off-topic response should be under 3 seconds");
    }
}
