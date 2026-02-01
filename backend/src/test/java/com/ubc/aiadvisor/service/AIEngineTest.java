package com.ubc.aiadvisor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

/**
 * AIEngineTest tests the AI engine error handling and edge cases.
 * Tests validation logic and error scenarios with mocked GeminiService.
 */
@ExtendWith(MockitoExtension.class)
class AIEngineTest {

    @Mock
    private GeminiService geminiService;

    private AIEngine aiEngine;

    @BeforeEach
    void setUp() throws Exception {
        aiEngine = new AIEngine(geminiService);
        
        // Setup default mock behavior - return valid JSON for normal cases (lenient to allow unused stubs)
        lenient().when(geminiService.generateContent(anyString(), any(File.class)))
            .thenReturn("{\"answer\": \"Mock answer\", \"citations\": [\"Mock citation\"]}");
    }

    @Test
    @DisplayName("generateAnswer handles null question")
    void generateAnswerNullQuestion() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();

        // Act
        AIEngineResult result = aiEngine.generateAnswer(null, testFile);

        // Assert
        assertNotNull(result);
        assertEquals("Please ask questions related to the course", result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles empty question")
    void generateAnswerEmptyQuestion() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();

        // Act
        AIEngineResult result = aiEngine.generateAnswer("", testFile);

        // Assert
        assertNotNull(result);
        assertEquals("Please ask questions related to the course", result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles whitespace-only question")
    void generateAnswerWhitespaceQuestion() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();

        // Act
        AIEngineResult result = aiEngine.generateAnswer("   ", testFile);

        // Assert
        assertNotNull(result);
        assertEquals("Please ask questions related to the course", result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles null file")
    void generateAnswerNullFile() {
        // Act
        AIEngineResult result = aiEngine.generateAnswer("What is the course about?", null);

        // Assert
        assertNotNull(result);
        assertEquals("No course document found. Please upload a syllabus first.", result.getAnswer());
    }

    @Test
    @DisplayName("generateAnswer handles non-existent file")
    void generateAnswerNonExistentFile() {
        // Arrange
        File nonExistentFile = new File("/path/to/nonexistent/file.pdf");

        // Act
        AIEngineResult result = aiEngine.generateAnswer("What is the course about?", nonExistentFile);

        // Assert
        assertNotNull(result);
        assertEquals("No course document found. Please upload a syllabus first.", result.getAnswer());
    }

    @Test
    @DisplayName("generateAnswer handles question with special characters")
    void generateAnswerSpecialCharacters() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();

        // Act
        AIEngineResult result = aiEngine.generateAnswer("What's the @#$% deadline?!", testFile);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles unicode characters in question")
    void generateAnswerUnicodeCharacters() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();

        // Act
        AIEngineResult result = aiEngine.generateAnswer("课程内容是什么？", testFile);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles very long question")
    void generateAnswerVeryLongQuestion() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();
        String longQuestion = "What ".repeat(500) + "is this course about?";

        // Act
        AIEngineResult result = aiEngine.generateAnswer(longQuestion, testFile);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        
        testFile.delete();
    }

    @Test
    @DisplayName("generateAnswer handles empty PDF file")
    void generateAnswerEmptyPdf() throws IOException {
        // Arrange
        File emptyFile = File.createTempFile("empty", ".pdf");
        emptyFile.deleteOnExit();

        // Act
        AIEngineResult result = aiEngine.generateAnswer("What is this course?", emptyFile);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        
        emptyFile.delete();
    }

    @Test
    @DisplayName("extractCourseName handles null file")
    void extractCourseNameNullFile() {
        // Act
        String courseName = aiEngine.extractCourseName(null);

        // Assert
        assertEquals("Unknown Course", courseName);
    }

    @Test
    @DisplayName("extractCourseName handles non-existent file")
    void extractCourseNameNonExistentFile() {
        // Arrange
        File nonExistentFile = new File("/path/to/nonexistent/file.pdf");

        // Act
        String courseName = aiEngine.extractCourseName(nonExistentFile);

        // Assert
        assertEquals("Unknown Course", courseName);
    }

    @Test
    @DisplayName("extractCourseName extracts course name successfully")
    void extractCourseNameSuccess() throws IOException {
        // Arrange
        File testFile = createTempPdfFile();
        lenient().when(geminiService.generateContent(anyString(), any(File.class)))
            .thenReturn("CPEN 221: Software Construction");

        // Act
        String courseName = aiEngine.extractCourseName(testFile);

        // Assert
        assertEquals("CPEN 221: Software Construction", courseName);
        
        testFile.delete();
    }

    // Helper method to create a temporary PDF file for testing
    private File createTempPdfFile() throws IOException {
        File tempFile = File.createTempFile("test", ".pdf");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), "Test PDF content".getBytes());
        return tempFile;
    }
}
