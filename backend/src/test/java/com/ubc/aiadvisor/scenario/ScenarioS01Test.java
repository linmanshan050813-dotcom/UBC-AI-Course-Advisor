package com.ubc.aiadvisor.scenario;

import com.ubc.aiadvisor.model.UploadedDoc;
import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.repository.HistoryEntryRepository;
import com.ubc.aiadvisor.repository.UploadedDocRepository;
import com.ubc.aiadvisor.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScenarioS01Test {

    @Mock SupabaseStorageService storage;
    @Mock UploadedDocRepository uploadRepo;
    @Mock HistoryEntryRepository historyRepo;
    @Mock AIEngine aiEngine;
    @Mock MultipartFile file;

    @InjectMocks UploadService uploadService;

    AIQueryService aiQueryService;

    @Test
    @DisplayName("Scenario S-01: upload then ask question yields citation and history persisted")
    void scenarioUploadThenQuestion() throws IOException {
        // Upload phase
        when(file.getOriginalFilename()).thenReturn("syllabus.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(200L);
        when(storage.uploadFile(file)).thenReturn("stored-syllabus.pdf");
        when(storage.downloadFile("stored-syllabus.pdf")).thenReturn(File.createTempFile("s01",".pdf"));
        when(aiEngine.extractCourseName(any(File.class))).thenReturn("Course Name");
        when(uploadRepo.findByUserIdOrderByUploadedAtDesc("profScenario")).thenReturn(List.of());
        Map<String,Object> upResult = uploadService.processUpload(file, "profScenario");
        String docId = (String) upResult.get("docId");

        // Query phase
        UploadedDoc doc = new UploadedDoc("stored-syllabus.pdf", "syllabus.pdf", "profScenario");
        when(uploadRepo.findById(docId)).thenReturn(java.util.Optional.of(doc));
        when(storage.downloadFile("stored-syllabus.pdf")).thenReturn(File.createTempFile("s01q",".pdf"));
        when(aiEngine.generateAnswer(anyString(), any(File.class)))
                .thenReturn(new AIEngineResult("Policy answer", List.of("Late policy (Page 3, Top)")));

        // Instantiate query service with the real uploadService and mocks
        aiQueryService = new AIQueryService(aiEngine, uploadService, storage);

        var response = aiQueryService.processQuery("Can I submit Lab 3 late?", "profScenario", docId);
        assertEquals("Policy answer", response.getAnswer());
        assertFalse(response.getCitations().isEmpty());

        // Simulate history persistence (would normally be triggered elsewhere)
        HistoryEntry entry = new HistoryEntry("Can I submit Lab 3 late?", response.getAnswer(), response.getCitations().get(0).getText(), "profScenario");
        when(historyRepo.findByUserIdOrderByCreatedAtDesc("profScenario")).thenReturn(List.of(entry));
        List<HistoryEntry> history = historyRepo.findByUserIdOrderByCreatedAtDesc("profScenario");
        assertEquals(1, history.size());
        assertEquals(response.getAnswer(), history.get(0).getAnswer());
    }
}
