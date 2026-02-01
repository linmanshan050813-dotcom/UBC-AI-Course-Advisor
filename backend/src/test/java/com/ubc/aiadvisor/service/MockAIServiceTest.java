package com.ubc.aiadvisor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MockAIServiceTest tests the mock AI service implementation.
 * Verifies deterministic mock responses and error handling.
 */
class MockAIServiceTest {

    private MockAIService mockAIService;

    @BeforeEach
    void setUp() {
        mockAIService = new MockAIService();
    }

    @Test
    @DisplayName("generateAnswer returns mock answer for grading question")
    void generateAnswerGradingQuestion() {
        // Arrange
        String question = "How is grading calculated?";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertTrue(result.getAnswer().toLowerCase().contains("grading"));
        assertNotNull(result.getCitations());
        assertFalse(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("generateAnswer returns mock answer for deadlines question")
    void generateAnswerDeadlinesQuestion() {
        // Arrange
        String question = "What are the project deadlines?";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertFalse(result.getAnswer().isEmpty());
        assertNotNull(result.getCitations());
        assertFalse(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("generateAnswer returns mock answer for office hours question")
    void generateAnswerOfficeHoursQuestion() {
        // Arrange
        String question = "When are office hours?";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertTrue(result.getAnswer().toLowerCase().contains("office"));
        assertNotNull(result.getCitations());
        assertFalse(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("generateAnswer returns mock answer for prerequisites question")
    void generateAnswerPrerequisitesQuestion() {
        // Arrange
        String question = "What are the prerequisites?";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertFalse(result.getAnswer().isEmpty());
        assertNotNull(result.getCitations());
        assertFalse(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("generateAnswer returns default mock answer for general question")
    void generateAnswerGeneralQuestion() {
        // Arrange
        String question = "Tell me about this course";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertNotNull(result.getCitations());
    }

    @Test
    @DisplayName("generateAnswer returns AIEngineResult object")
    void generateAnswerReturnsResult() {
        // Arrange
        String question = "What are the prerequisites?";

        // Act
        AIEngineResult result = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertNotNull(result.getCitations());
    }

    @Test
    @DisplayName("generateAnswer handles null question gracefully")
    void generateAnswerNullQuestion() {
        // Act
        AIEngineResult result = mockAIService.generateAnswer(null);

        // Assert
        assertNotNull(result);
        assertEquals("Please ask course-related questions.", result.getAnswer());
    }

    @Test
    @DisplayName("generateAnswer handles empty question gracefully")
    void generateAnswerEmptyQuestion() {
        // Act
        AIEngineResult result = mockAIService.generateAnswer("");

        // Assert
        assertNotNull(result);
        assertEquals("Please ask course-related questions.", result.getAnswer());
    }

    @Test
    @DisplayName("generateAnswer caches responses")
    void generateAnswerCachesResponses() {
        // Arrange
        String question = "How is grading calculated?";

        // Act
        AIEngineResult result1 = mockAIService.generateAnswer(question);
        AIEngineResult result2 = mockAIService.generateAnswer(question);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertSame(result1, result2); // Same object due to caching
    }

    @Test
    @DisplayName("generateAnswer returns different responses for different questions")
    void generateAnswerDifferentResponses() {
        // Act
        AIEngineResult result1 = mockAIService.generateAnswer("How is grading calculated?");
        AIEngineResult result2 = mockAIService.generateAnswer("What are the project deadlines?");

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getAnswer(), result2.getAnswer());
    }

    @Test
    @DisplayName("generateAnswer always includes citation information")
    void generateAnswerAlwaysHasCitation() {
        // Arrange
        String[] questions = {
                "How is grading calculated?",
                "What are the deadlines?",
                "When are office hours?",
                "What textbook is required?",
                "Random question here"
        };

        // Act & Assert
        for (String question : questions) {
            AIEngineResult result = mockAIService.generateAnswer(question);
            assertNotNull(result);
            assertNotNull(result.getCitations(), "Response for '" + question + "' should contain citations");
        }
    }

    @Test
    @DisplayName("generateAnswer is case-insensitive for keywords")
    void generateAnswerCaseInsensitive() {
        // Act
        AIEngineResult result1 = mockAIService.generateAnswer("GRADING policy");
        AIEngineResult result2 = mockAIService.generateAnswer("grading policy");

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.getAnswer().toLowerCase().contains("grading"));
        assertTrue(result2.getAnswer().toLowerCase().contains("grading"));
    }

    @Test
    @DisplayName("clearCache empties the response cache")
    void clearCacheEmptiesCache() {
        // Arrange
        mockAIService.generateAnswer("How is grading calculated?");
        mockAIService.generateAnswer("What are deadlines?");
        int sizeBeforeClear = mockAIService.getCacheSize();

        // Act
        mockAIService.clearCache();

        // Assert
        assertTrue(sizeBeforeClear > 0);
        assertEquals(0, mockAIService.getCacheSize());
    }

    @Test
    @DisplayName("getCacheSize returns correct count")
    void getCacheSizeReturnsCount() {
        // Arrange
        mockAIService.clearCache();

        // Act & Assert
        assertEquals(0, mockAIService.getCacheSize());
        mockAIService.generateAnswer("How is grading calculated?");
        assertEquals(1, mockAIService.getCacheSize());
        mockAIService.generateAnswer("What are deadlines?");
        assertEquals(2, mockAIService.getCacheSize());
    }
}
