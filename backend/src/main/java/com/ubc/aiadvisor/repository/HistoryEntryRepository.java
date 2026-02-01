package com.ubc.aiadvisor.repository;

import com.ubc.aiadvisor.model.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * History Entry Repository (DUT)
 * Spring Data JPA repository interface for HistoryEntry entity.
 * Provides CRUD operations and pagination support.
 * Used in Phase 3+ of the AI Course Advisor project.
 */
import java.util.List;

@Repository
public interface HistoryEntryRepository extends JpaRepository<HistoryEntry, String> {
    
    /**
     * Find all history entries for a specific user, ordered by creation time (newest first).
     * 
     * @param userId user ID to filter by
     * @return list of history entries
     */
    List<HistoryEntry> findByUserIdOrderByCreatedAtDesc(String userId);
}
