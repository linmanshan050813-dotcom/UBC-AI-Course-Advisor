package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import com.ubc.aiadvisor.repository.UploadedDocRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UploadServiceReplacementTest {

    @Mock
    SupabaseStorageService supabaseStorageService;

    @Mock
    UploadedDocRepository uploadedDocRepository;

    @Mock
    AIEngine aiEngine;

    @InjectMocks
    UploadService uploadService;

    @Mock
    MultipartFile file1;
    @Mock
    MultipartFile file2;

    @Test
    @DisplayName("Second upload of same course replaces previous index (R-U-04/S-03)")
    void replacementRemovesPreviousCourseIndex() throws IOException {
        when(file1.getOriginalFilename()).thenReturn("syllabus.pdf");
        when(file1.getContentType()).thenReturn("application/pdf");
        when(file1.getSize()).thenReturn(100L);

        when(file2.getOriginalFilename()).thenReturn("syllabus_updated.pdf");
        when(file2.getContentType()).thenReturn("application/pdf");
        when(file2.getSize()).thenReturn(120L);

        when(supabaseStorageService.uploadFile(file1)).thenReturn("f1.pdf");
        when(supabaseStorageService.uploadFile(file2)).thenReturn("f2.pdf");
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("tmp",".pdf"));

        when(aiEngine.extractCourseName(any(File.class))).thenReturn("CPEN 221: Software Construction");

        // First upload: no existing docs
        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc("prof1")).thenReturn(List.of());
        uploadService.processUpload(file1, "prof1");

        UploadedDoc existingDoc = new UploadedDoc("f1.pdf", "syllabus.pdf", "prof1");
        existingDoc.setCourseName("CPEN 221: Software Construction");

        // Second upload: repository returns previous doc
        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc("prof1")).thenReturn(List.of(existingDoc));
        Map<String, Object> result2 = uploadService.processUpload(file2, "prof1");

        assertTrue((Boolean) result2.get("success"));
        verify(uploadedDocRepository, times(1)).delete(existingDoc); // previous removed
        verify(uploadedDocRepository, times(2)).save(any(UploadedDoc.class)); // first + second
        verify(supabaseStorageService, times(1)).deleteFile("f1.pdf");
    }

    @Test
    @DisplayName("Different course name does not replace previous index")
    void differentCourseDoesNotReplace() throws IOException {
        when(file1.getOriginalFilename()).thenReturn("syllabusA.pdf");
        when(file1.getContentType()).thenReturn("application/pdf");
        when(file1.getSize()).thenReturn(100L);

        when(file2.getOriginalFilename()).thenReturn("syllabusB.pdf");
        when(file2.getContentType()).thenReturn("application/pdf");
        when(file2.getSize()).thenReturn(100L);

        when(supabaseStorageService.uploadFile(file1)).thenReturn("fa.pdf");
        when(supabaseStorageService.uploadFile(file2)).thenReturn("fb.pdf");
        when(supabaseStorageService.downloadFile(anyString())).thenReturn(File.createTempFile("tmp",".pdf"));

        when(aiEngine.extractCourseName(any(File.class)))
                .thenReturn("Course A")
                .thenReturn("Course B");

        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc("prof2")).thenReturn(List.of());
        uploadService.processUpload(file1, "prof2");

        UploadedDoc existingA = new UploadedDoc("fa.pdf", "syllabusA.pdf", "prof2");
        existingA.setCourseName("Course A");

        when(uploadedDocRepository.findByUserIdOrderByUploadedAtDesc("prof2")).thenReturn(List.of(existingA));
        uploadService.processUpload(file2, "prof2");

        verify(uploadedDocRepository, never()).delete(existingA);
        verify(supabaseStorageService, never()).deleteFile("fa.pdf");
    }
}
