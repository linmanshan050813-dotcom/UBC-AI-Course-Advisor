package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.repository.HistoryEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryEntryRepository historyEntryRepository;

    @InjectMocks
    private HistoryService historyService;

    @Test
    @DisplayName("getUserHistory returns list of history entries sorted by newest first")
    void getUserHistorySuccess() {
        // Arrange
        String userId = "testUser";
        HistoryEntry entry1 = new HistoryEntry("Q1", "A1", null, userId);
        HistoryEntry entry2 = new HistoryEntry("Q2", "A2", null, userId);
        List<HistoryEntry> entries = Arrays.asList(entry1, entry2);
        
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(entries);

        // Act
        List<HistoryEntry> result = historyService.getUserHistory(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(historyEntryRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("addEntry saves new history entry with timestamp")
    void addEntrySuccess() {
        // Arrange
        String question = "What is CPSC 210?";
        String answer = "Software construction course";
        String citations = "Page 1";
        String userId = "testUser";
        HistoryEntry savedEntry = new HistoryEntry(question, answer, citations, userId);
        
        when(historyEntryRepository.save(any(HistoryEntry.class))).thenReturn(savedEntry);
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Arrays.asList(savedEntry));

        // Act
        HistoryEntry result = historyService.addEntry(question, answer, citations, userId);

        // Assert
        assertNotNull(result);
        assertEquals(question, result.getQuestion());
        assertEquals(answer, result.getAnswer());
        verify(historyEntryRepository).save(any(HistoryEntry.class));
    }

    @Test
    @DisplayName("addEntry throws exception for null question")
    void addEntryNullQuestion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            historyService.addEntry(null, "answer", null, "testUser");
        });
    }

    @Test
    @DisplayName("addEntry throws exception for empty question")
    void addEntryEmptyQuestion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            historyService.addEntry("", "answer", null, "testUser");
        });
    }

    @Test
    @DisplayName("addEntry throws exception for null answer")
    void addEntryNullAnswer() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            historyService.addEntry("question", null, null, "testUser");
        });
    }

    @Test
    @DisplayName("addEntry throws exception for empty answer")
    void addEntryEmptyAnswer() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            historyService.addEntry("question", "", null, "testUser");
        });
    }

    @Test
    @DisplayName("addEntry removes oldest entry when limit exceeded")
    void addEntryRemovesOldest() {
        // Arrange
        String question = "New question";
        String answer = "New answer";
        String userId = "testUser";
        HistoryEntry newEntry = new HistoryEntry(question, answer, null, userId);
        
        // Create 11 entries to exceed limit
        List<HistoryEntry> existingEntries = Arrays.asList(
            new HistoryEntry("Q1", "A1", null, userId),
            new HistoryEntry("Q2", "A2", null, userId),
            new HistoryEntry("Q3", "A3", null, userId),
            new HistoryEntry("Q4", "A4", null, userId),
            new HistoryEntry("Q5", "A5", null, userId),
            new HistoryEntry("Q6", "A6", null, userId),
            new HistoryEntry("Q7", "A7", null, userId),
            new HistoryEntry("Q8", "A8", null, userId),
            new HistoryEntry("Q9", "A9", null, userId),
            new HistoryEntry("Q10", "A10", null, userId),
            new HistoryEntry("Q11", "A11", null, userId)
        );
        
        when(historyEntryRepository.save(any(HistoryEntry.class))).thenReturn(newEntry);
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(existingEntries);

        // Act
        historyService.addEntry(question, answer, null, userId);

        // Assert
        verify(historyEntryRepository).deleteAll(anyList());
    }

    @Test
    @DisplayName("addEntry does not remove entries when under limit")
    void addEntryNoRemoval() {
        // Arrange
        String userId = "testUser";
        HistoryEntry entry = new HistoryEntry("Q", "A", null, userId);
        
        when(historyEntryRepository.save(any(HistoryEntry.class))).thenReturn(entry);
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId))
            .thenReturn(Arrays.asList(entry)); // Only 1 entry

        // Act
        historyService.addEntry("Q", "A", null, userId);

        // Assert
        verify(historyEntryRepository, never()).deleteAll(anyList());
    }

    @Test
    @DisplayName("deleteHistoryEntry deletes entry for authorized user")
    void deleteHistoryEntrySuccess() {
        // Arrange
        String entryId = "entry123";
        String userId = "testUser";
        HistoryEntry entry = new HistoryEntry("Q", "A", null, userId);
        
        when(historyEntryRepository.findById(entryId)).thenReturn(Optional.of(entry));

        // Act
        historyService.deleteHistoryEntry(entryId, userId);

        // Assert
        verify(historyEntryRepository).delete(entry);
    }

    @Test
    @DisplayName("clearHistory removes all entries for user")
    void clearHistorySuccess() {
        // Arrange
        String userId = "testUser";
        List<HistoryEntry> entries = Arrays.asList(
            new HistoryEntry("Q1", "A1", null, userId),
            new HistoryEntry("Q2", "A2", null, userId)
        );
        
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(entries);

        // Act
        historyService.clearHistory(userId);

        // Assert
        verify(historyEntryRepository).deleteAll(entries);
    }
}
