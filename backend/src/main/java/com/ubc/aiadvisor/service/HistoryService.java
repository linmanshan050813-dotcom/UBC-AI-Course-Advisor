package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.repository.HistoryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * History Service (DUT)
 * Manages Q/A history entries for each user.
 * Maintains maximum of 10 items per user.
 * Used in Phase 3+ of the AI Course Advisor project.
 */
@Service
public class HistoryService {

    private static final int MAX_HISTORY_ITEMS = 10;
    
    private final HistoryEntryRepository historyEntryRepository;

    @Autowired
    public HistoryService(HistoryEntryRepository historyEntryRepository) {
        this.historyEntryRepository = historyEntryRepository;
    }

    /**
     * Retrieve the last 10 history entries for a specific user.
     * 
     * @param userId the user ID
     * @return list of history entries sorted newest to oldest
     */
    public List<HistoryEntry> getUserHistory(String userId) {
        return historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Delete a specific history entry.
     * Verifies ownership before deletion.
     * 
     * @param id history entry ID
     * @param userId user ID requesting deletion
     */
    @Transactional
    public void deleteHistoryEntry(String id, String userId) {
        historyEntryRepository.findById(id).ifPresent(entry -> {
            if (entry.getUserId().equals(userId)) {
                historyEntryRepository.delete(entry);
            }
        });
    }

    /**
     * Clear all history entries for a user.
     * 
     * @param userId user ID
     */
    @Transactional
    public void clearHistory(String userId) {
        List<HistoryEntry> entries = historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        historyEntryRepository.deleteAll(entries);
    }

    /**
     * Add a new Q/A history entry and enforce maximum item limit per user.
     * 
     * @param question the user's question string
     * @param answer the AI-generated answer string
     * @param userId the user ID
     * @return the created and saved history entry
     */
    @Transactional
    public HistoryEntry addEntry(String question, String answer, String citations, String userId) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty");
        }

        // Create new entry
        HistoryEntry entry = new HistoryEntry(question, answer, citations, userId);
        entry.setCreatedAt(LocalDateTime.now());
        entry = historyEntryRepository.save(entry);

        // Enforce limit per user
        List<HistoryEntry> userHistory = historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (userHistory.size() > MAX_HISTORY_ITEMS) {
            // The list is sorted newest first. 
            // So we want to keep index 0 to MAX-1.
            // We should delete index MAX to end.
            
            List<HistoryEntry> toDelete = userHistory.subList(MAX_HISTORY_ITEMS, userHistory.size());
            historyEntryRepository.deleteAll(toDelete);
        }

        return entry;
    }
}
