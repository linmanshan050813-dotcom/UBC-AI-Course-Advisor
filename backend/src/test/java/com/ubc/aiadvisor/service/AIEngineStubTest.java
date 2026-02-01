package com.ubc.aiadvisor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AIEngineStubTest tests the AIEngineResult data structure.
 * This verifies that AIEngineResult correctly stores answers and citations.
 */
class AIEngineStubTest {

    @Test
    @DisplayName("AIEngineResult stores answer and citations correctly")
    void aiEngineResultCreation() {
        // Arrange
        String answer = "This is an answer";
        List<String> citations = Arrays.asList("Citation 1 (Page 1)", "Citation 2 (Page 2)", "Citation 3 (Page 3)");

        // Act
        AIEngineResult result = new AIEngineResult(answer, citations);

        // Assert
        assertNotNull(result);
        assertEquals(answer, result.getAnswer());
        assertEquals(3, result.getCitations().size());
        assertEquals("Citation 1 (Page 1)", result.getCitations().get(0));
    }

    @Test
    @DisplayName("AIEngineResult handles empty citations list")
    void aiEngineResultEmptyCitations() {
        // Arrange
        String answer = "Answer without citations";
        List<String> citations = Arrays.asList();

        // Act
        AIEngineResult result = new AIEngineResult(answer, citations);

        // Assert
        assertNotNull(result);
        assertEquals(answer, result.getAnswer());
        assertTrue(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("AIEngineResult handles null citations list")
    void aiEngineResultNullCitations() {
        // Arrange
        String answer = "Answer";

        // Act
        AIEngineResult result = new AIEngineResult(answer, null);

        // Assert
        assertNotNull(result);
        assertEquals(answer, result.getAnswer());
        // Citations list should be initialized to empty list, never null
        assertNotNull(result.getCitations());
        assertTrue(result.getCitations().isEmpty());
    }

    @Test
    @DisplayName("AIEngineResult with multiple citations preserves order")
    void aiEngineResultMultipleCitations() {
        // Arrange
        List<String> citations = Arrays.asList(
            "Quote from page 3 (Page 3, Top)",
            "Quote from page 1 (Page 1, Middle)",
            "Quote from page 2 (Page 2, Bottom)",
            "Quote from page 4 (Page 4, Top)"
        );

        // Act
        AIEngineResult result = new AIEngineResult("Test answer", citations);

        // Assert
        assertEquals(4, result.getCitations().size());
        assertEquals("Quote from page 3 (Page 3, Top)", result.getCitations().get(0));
        assertEquals("Quote from page 1 (Page 1, Middle)", result.getCitations().get(1));
        assertEquals("Quote from page 2 (Page 2, Bottom)", result.getCitations().get(2));
        assertEquals("Quote from page 4 (Page 4, Top)", result.getCitations().get(3));
    }
}
