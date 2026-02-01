package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQueryServiceAccessControlTest {
    @Mock AIEngine aiEngine;
    @Mock UploadService uploadService;
    @Mock SupabaseStorageService supabaseStorageService;

    @InjectMocks AIQueryService aiQueryService;

    @Test
    @DisplayName("Student cannot query another user's document (R-N-03 extension)")
    void unauthorizedQueryOtherDoc() throws Exception {
        String ownerId = "profUser";
        String otherUser = "studentUser";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored.pdf","original.pdf", ownerId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        // Attempt by otherUser should yield unauthorized message
        assertEquals("Unauthorized access to document.", aiQueryService.processQuery("Any policy?", otherUser, docId).getAnswer());
    }

    @Test
    @DisplayName("Owner can query their document")
    void ownerQueriesDoc() throws Exception {
        String ownerId = "profUser";
        String docId = UUID.randomUUID().toString();
        UploadedDoc doc = new UploadedDoc("stored2.pdf","original2.pdf", ownerId);
        when(uploadService.getUploadById(docId)).thenReturn(doc);
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("acx",".pdf"));
        when(aiEngine.generateAnswer(anyString(), org.mockito.ArgumentMatchers.any(File.class)))
                .thenReturn(new AIEngineResult("Owner answer", java.util.List.of("Citation")));
        assertEquals("Owner answer", aiQueryService.processQuery("Late policy?", ownerId, docId).getAnswer());
    }
}
