package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.repository.UploadedDocRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class UploadServiceTimingTest {

    @Mock SupabaseStorageService storage;
    @Mock UploadedDocRepository repo;
    @Mock AIEngine aiEngine;
    @Mock MultipartFile file;

    @InjectMocks UploadService uploadService;

    @Test
    @DisplayName("Upload parse/store completes under 5s (R-U-02)")
    void uploadCompletesUnderFiveSeconds() throws IOException {
        when(file.getOriginalFilename()).thenReturn("course.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(200L);
        when(storage.uploadFile(file)).thenAnswer(inv -> { Thread.sleep(50); return "stored-course.pdf"; });
        when(storage.downloadFile(anyString())).thenAnswer(inv -> { Thread.sleep(50); return File.createTempFile("utime",".pdf"); });
        when(aiEngine.extractCourseName(any(File.class))).thenAnswer(inv -> { Thread.sleep(50); return "Course Name"; });
        when(repo.findByUserIdOrderByUploadedAtDesc("userT")).thenReturn(List.of());

        Instant start = Instant.now();
        uploadService.processUpload(file, "userT");
        long ms = Duration.between(start, Instant.now()).toMillis();
        assertTrue(ms < 5000, "Upload processing exceeded 5s: " + ms);
    }
}
