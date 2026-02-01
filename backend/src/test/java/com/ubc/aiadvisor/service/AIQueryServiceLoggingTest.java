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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIQueryServiceLoggingTest {

    @InjectMocks
    AIQueryService aiQueryService;

    @Mock
    AIEngine aiEngine;

    @Mock
    UploadService uploadService;

    @Mock
    SupabaseStorageService supabaseStorageService;

    @Test
    @DisplayName("processQuery logs error and returns fallback when AI throws")
    void processQueryLogsErrorOnFailure() throws Exception {
        String userId = "log-user";
        UploadedDoc doc = new UploadedDoc("log.pdf", "orig.pdf", userId);
        when(uploadService.getUploadById(anyString())).thenReturn(doc);
        when(uploadService.getUserUploads(userId)).thenReturn(List.of(doc));
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("log",".pdf"));
        when(aiEngine.generateAnswer(anyString(), any(File.class))).thenThrow(new RuntimeException("AI failure simulation"));

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        QueryResponse response = aiQueryService.processQuery("Trigger failure?", userId, doc.getId());

        System.setErr(originalErr);

        assertNotNull(response);
        assertTrue(response.getAnswer().toLowerCase().contains("error"));
        String logs = errContent.toString();
        assertTrue(logs.contains("ERROR: Query processing failed"));
        assertTrue(logs.contains("AI failure simulation"));
    }
}
