package com.ubc.aiadvisor.service;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SupabaseStorageServiceTest tests file operations with Supabase Storage.
 * Uses mocked HTTP client to avoid real API calls during testing.
 */
@ExtendWith(MockitoExtension.class)
class SupabaseStorageServiceTest {

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private SupabaseStorageService supabaseStorageService;

    @BeforeEach
    void setUp() {
        supabaseStorageService = new SupabaseStorageService();
        ReflectionTestUtils.setField(supabaseStorageService, "supabaseUrl", "https://test.supabase.co");
        ReflectionTestUtils.setField(supabaseStorageService, "serviceKey", "test-service-key");
        ReflectionTestUtils.setField(supabaseStorageService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(supabaseStorageService, "httpClient", mockHttpClient);
    }

    @Test
    @DisplayName("uploadFile successfully uploads file and returns filename")
    void uploadFileSuccess() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        String result = supabaseStorageService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".pdf"));
        verify(mockHttpClient).newCall(any(Request.class));
        verify(mockCall).execute();
    }

    @Test
    @DisplayName("uploadFile throws IOException when upload fails")
    void uploadFileThrowsExceptionOnFailure() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("Upload error");

        // Act & Assert
        assertThrows(IOException.class, () -> supabaseStorageService.uploadFile(file));
    }

    @Test
    @DisplayName("uploadFile handles file without extension")
    void uploadFileWithoutExtension() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "testfile",
                "application/octet-stream",
                "test content".getBytes()
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        String result = supabaseStorageService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertFalse(result.contains(".")); // No extension
    }

    @Test
    @DisplayName("uploadFile handles null original filename")
    void uploadFileWithNullFilename() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "application/pdf",
                "test content".getBytes()
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        String result = supabaseStorageService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("downloadFile successfully downloads and creates temp file")
    void downloadFileSuccess() throws IOException {
        // Arrange
        String filename = "test-file.pdf";
        byte[] fileContent = "downloaded content".getBytes();

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.byteStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        File result = supabaseStorageService.downloadFile(filename);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().contains(filename));
        verify(mockHttpClient).newCall(any(Request.class));
    }

    @Test
    @DisplayName("downloadFile throws IOException when download fails")
    void downloadFileThrowsExceptionOnFailure() throws IOException {
        // Arrange
        String filename = "nonexistent.pdf";

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(404);
        when(mockResponse.message()).thenReturn("Not Found");
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("File not found");

        // Act & Assert
        assertThrows(IOException.class, () -> supabaseStorageService.downloadFile(filename));
    }

    @Test
    @DisplayName("downloadFile throws IOException when response body is null")
    void downloadFileThrowsExceptionForNullBody() throws IOException {
        // Arrange
        String filename = "test.pdf";

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(null);

        // Act & Assert
        assertThrows(IOException.class, () -> supabaseStorageService.downloadFile(filename));
    }

    @Test
    @DisplayName("deleteFile successfully deletes file")
    void deleteFileSuccess() throws IOException {
        // Arrange
        String filename = "file-to-delete.pdf";

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> supabaseStorageService.deleteFile(filename));

        // Assert
        verify(mockHttpClient).newCall(any(Request.class));
        verify(mockCall).execute();
    }

    @Test
    @DisplayName("deleteFile throws IOException when deletion fails")
    void deleteFileThrowsExceptionOnFailure() throws IOException {
        // Arrange
        String filename = "protected-file.pdf";

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(403);

        // Act & Assert
        assertThrows(IOException.class, () -> supabaseStorageService.deleteFile(filename));
    }

    @Test
    @DisplayName("uploadFile preserves file extension correctly")
    void uploadFilePreservesExtension() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "content".getBytes()
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        String result = supabaseStorageService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".docx"));
    }

    @Test
    @DisplayName("uploadFile handles empty file")
    void uploadFileHandlesEmptyFile() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);

        // Act
        String result = supabaseStorageService.uploadFile(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".txt"));
    }
}
