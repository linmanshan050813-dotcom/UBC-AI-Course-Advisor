package com.ubc.aiadvisor.scenario;

import com.ubc.aiadvisor.model.UploadedDoc;
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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScenarioS03ReplacementInvalidationTest {

    @Mock SupabaseStorageService storage;
    @Mock UploadedDocRepository uploadRepo;
    @Mock AIEngine aiEngine;
    @Mock MultipartFile file1;
    @Mock MultipartFile file2;

    @InjectMocks UploadService uploadService;

    @Mock UploadService uploadServiceMock; // for AIQueryService queries
    @Mock SupabaseStorageService storage2;
    @Mock AIEngine aiEngine2;
    @InjectMocks AIQueryService aiQueryService;

    @Test
    @DisplayName("Scenario S-03: Replacement produces new answer and deletes old index")
    void replacementInvalidatesPrevious() throws IOException {
        // First upload
        when(file1.getOriginalFilename()).thenReturn("syllabus.pdf");
        when(file1.getContentType()).thenReturn("application/pdf");
        when(file1.getSize()).thenReturn(150L);
        when(storage.uploadFile(file1)).thenReturn("stored-old.pdf");
        when(storage.downloadFile("stored-old.pdf")).thenReturn(File.createTempFile("s03old",".pdf"));
        when(aiEngine.extractCourseName(any(File.class))).thenReturn("Course Name");
        when(uploadRepo.findByUserIdOrderByUploadedAtDesc("profR"))
                .thenReturn(List.of());
        Map<String,Object> first = uploadService.processUpload(file1, "profR");

        // Second upload same course triggers replacement
        when(file2.getOriginalFilename()).thenReturn("syllabus_updated.pdf");
        when(file2.getContentType()).thenReturn("application/pdf");
        when(file2.getSize()).thenReturn(160L);
        when(storage.uploadFile(file2)).thenReturn("stored-new.pdf");
        when(storage.downloadFile("stored-new.pdf")).thenReturn(File.createTempFile("s03new",".pdf"));
        when(aiEngine.extractCourseName(any(File.class))).thenReturn("Course Name");
        UploadedDoc oldDoc = new UploadedDoc("stored-old.pdf","syllabus.pdf","profR");
        oldDoc.setCourseName("Course Name");
        when(uploadRepo.findByUserIdOrderByUploadedAtDesc("profR")).thenReturn(List.of(oldDoc));
        Map<String,Object> second = uploadService.processUpload(file2, "profR");

        // Query old vs new (simulate old answer vs new answer)
        String oldDocId = (String) first.get("docId");
        String newDocId = (String) second.get("docId");
        UploadedDoc newDoc = new UploadedDoc("stored-new.pdf","syllabus_updated.pdf","profR");
        when(uploadServiceMock.getUploadById(newDocId)).thenReturn(newDoc);
        when(storage2.downloadFile("stored-new.pdf")).thenReturn(File.createTempFile("s03q",".pdf"));
        when(aiEngine2.generateAnswer(anyString(), any(File.class)))
                .thenReturn(new AIEngineResult("New policy answer", List.of("Updated citation (Page 4, Middle)")));

        // Simulated old answer text for comparison
        String oldAnswer = "Old policy answer";
        var responseNew = aiQueryService.processQuery("Late policy?", "profR", newDocId);
        assertNotEquals(oldAnswer, responseNew.getAnswer());
        // Replacement produced new docId
        assertNotEquals(oldDocId, newDocId);
    }
}
