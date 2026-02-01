package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.repository.UploadedDocRepository;
import com.ubc.aiadvisor.model.UploadedDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class UploadServiceFileTypesTest {

    @Mock SupabaseStorageService storage;
    @Mock UploadedDocRepository repo;
    @Mock AIEngine aiEngine;
    @InjectMocks UploadService uploadService;

    @Mock MultipartFile file;

    @ParameterizedTest
    @ValueSource(strings = {"course.pdf", "outline.txt", "syllabus.docx"})
    @DisplayName("Supported file types accepted (R-U-01)")
    void supportedTypesProcessUpload(String filename) throws IOException {
        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getContentType()).thenReturn("application/octet-stream");
        when(file.getSize()).thenReturn(120L);
        when(storage.uploadFile(file)).thenReturn("stored-" + filename);
        when(storage.downloadFile(anyString())).thenReturn(File.createTempFile("ftype",".pdf"));
        when(aiEngine.extractCourseName(any(File.class))).thenReturn("Course Name");
        when(repo.findByUserIdOrderByUploadedAtDesc("userF")).thenReturn(List.of());
        Map<String,Object> result = uploadService.processUpload(file, "userF");
        assertTrue((Boolean) result.get("success"));
        verify(repo, times(1)).save(any(UploadedDoc.class));
    }
}
