package com.ubc.aiadvisor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeminiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GeminiService geminiService;

    @BeforeEach
    void setUp() {
        geminiService = new GeminiService(restTemplate);
        ReflectionTestUtils.setField(geminiService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(geminiService, "apiUrl", "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
    }

    @Test
    void testGenerateContent_Success() throws IOException {
        String prompt = "Test prompt";
        String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Test response\"}]}}]}";

        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        String result = geminiService.generateContent(prompt, null);

        assertEquals("Test response", result);
        verify(restTemplate).postForObject(any(String.class), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testGenerateContent_MissingApiKey() {
        ReflectionTestUtils.setField(geminiService, "apiKey", null);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            geminiService.generateContent("prompt", null);
        });

        assertEquals("Gemini API Key is missing", exception.getMessage());
    }

    @Test
    void testGenerateContent_ApiError() {
        String prompt = "Test prompt";
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "Error details".getBytes(), null));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            geminiService.generateContent(prompt, null);
        });

        assertTrue(exception.getMessage().contains("Gemini API Error"));
        assertTrue(exception.getMessage().contains("Error details"));
    }

    @Test
    void testGenerateContent_WithTxtFile() throws IOException {
        String prompt = "Test prompt with file";
        File tempFile = File.createTempFile("test", ".txt");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("Hello world".getBytes(StandardCharsets.UTF_8));
        }

        String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Response with file\"}]}}]}";
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        String result = geminiService.generateContent(prompt, tempFile);

        assertEquals("Response with file", result);
        tempFile.delete();
    }
    
    @Test
    void testGenerateContent_ResponseParsingError() throws IOException {
        String prompt = "Test prompt";
        String invalidJson = "{invalid json}";

        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(invalidJson);

        String result = geminiService.generateContent(prompt, null);

        // Expect raw response if parsing fails
        assertEquals(invalidJson, result);
    }
}
