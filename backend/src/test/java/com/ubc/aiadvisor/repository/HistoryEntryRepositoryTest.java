package com.ubc.aiadvisor.repository;

import com.ubc.aiadvisor.model.HistoryEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HistoryEntryRepositoryTest verifies JPA repository operations for HistoryEntry.
 * Uses H2 in-memory database for testing.
 */
@DataJpaTest
@ActiveProfiles("test")
class HistoryEntryRepositoryTest {

    @Autowired
    private HistoryEntryRepository historyEntryRepository;

    @Test
    @DisplayName("Repository can save and retrieve history entry")
    void saveAndRetrieveHistoryEntry() {
        // Arrange
        HistoryEntry entry = new HistoryEntry("What is CPSC 210?", "Software construction course", null, "testUser");
        entry.setCreatedAt(LocalDateTime.now());

        // Act
        HistoryEntry saved = historyEntryRepository.save(entry);
        List<HistoryEntry> all = historyEntryRepository.findAll();

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertTrue(all.size() > 0);
    }

    @Test
    @DisplayName("Repository supports pagination and sorting")
    void paginationAndSorting() {
        // Arrange
        HistoryEntry entry1 = new HistoryEntry("Q1", "A1", null, "testUser");
        entry1.setCreatedAt(LocalDateTime.now().minusHours(2));
        HistoryEntry entry2 = new HistoryEntry("Q2", "A2", null, "testUser");
        entry2.setCreatedAt(LocalDateTime.now().minusHours(1));
        HistoryEntry entry3 = new HistoryEntry("Q3", "A3", null, "testUser");
        entry3.setCreatedAt(LocalDateTime.now());

        historyEntryRepository.save(entry1);
        historyEntryRepository.save(entry2);
        historyEntryRepository.save(entry3);

        // Act
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<HistoryEntry> page = historyEntryRepository.findAll(pageable);

        // Assert
        assertNotNull(page);
        assertEquals(2, page.getContent().size());
        // Most recent should be first
        assertEquals("Q3", page.getContent().get(0).getQuestion());
    }

    @Test
    @DisplayName("Repository can delete entries")
    void deleteEntries() {
        // Arrange
        HistoryEntry entry = new HistoryEntry("Delete test", "Answer", null, "testUser");
        HistoryEntry saved = historyEntryRepository.save(entry);
        long initialCount = historyEntryRepository.count();

        // Act
        historyEntryRepository.delete(saved);
        long finalCount = historyEntryRepository.count();

        // Assert
        assertEquals(initialCount - 1, finalCount);
    }

    @Test
    @DisplayName("Repository can count entries")
    void countEntries() {
        // Arrange
        historyEntryRepository.deleteAll();
        HistoryEntry entry1 = new HistoryEntry("Q1", "A1", null, "testUser");
        HistoryEntry entry2 = new HistoryEntry("Q2", "A2", null, "testUser");
        
        // Act
        historyEntryRepository.save(entry1);
        historyEntryRepository.save(entry2);
        long count = historyEntryRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Repository can delete all entries")
    void deleteAllEntries() {
        // Arrange
        historyEntryRepository.save(new HistoryEntry("Q1", "A1", null, "testUser"));
        historyEntryRepository.save(new HistoryEntry("Q2", "A2", null, "testUser"));

        // Act
        historyEntryRepository.deleteAll();
        long count = historyEntryRepository.count();

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Repository preserves question and answer content")
    void preservesContent() {
        // Arrange
        String question = "What is the best programming language?";
        String answer = "It depends on the use case and context.";
        HistoryEntry entry = new HistoryEntry(question, answer, null, "testUser");

        // Act
        HistoryEntry saved = historyEntryRepository.save(entry);
        HistoryEntry retrieved = historyEntryRepository.findAll().stream()
            .filter(e -> e.getId().equals(saved.getId()))
            .findFirst()
            .orElse(null);

        // Assert
        assertNotNull(retrieved);
        assertEquals(question, retrieved.getQuestion());
        assertEquals(answer, retrieved.getAnswer());
    }

    @Test
    @DisplayName("Repository handles long text content")
    void handlesLongText() {
        // Arrange
        String longQuestion = "Q".repeat(1000);
        String longAnswer = "A".repeat(2000);
        HistoryEntry entry = new HistoryEntry(longQuestion, longAnswer, null, "testUser");

        // Act
        HistoryEntry saved = historyEntryRepository.save(entry);

        // Assert
        assertNotNull(saved);
        assertEquals(longQuestion, saved.getQuestion());
        assertEquals(longAnswer, saved.getAnswer());
    }
}
