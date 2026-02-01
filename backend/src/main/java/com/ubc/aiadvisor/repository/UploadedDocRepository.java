package com.ubc.aiadvisor.repository;

import com.ubc.aiadvisor.model.UploadedDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Uploaded Document Repository (DUT)
 * Spring Data JPA repository interface for UploadedDoc entity.
 * Provides access to stored document metadata.
 * Used in Phase 1+ of the AI Course Advisor project.
 */
@Repository
public interface UploadedDocRepository extends JpaRepository<UploadedDoc, String> {
    
    /**
     * Find all documents uploaded by a specific user, ordered by upload time (newest first).
     * 
     * @param userId user ID to filter by
     * @return list of uploaded documents
     */
    List<UploadedDoc> findByUserIdOrderByUploadedAtDesc(String userId);
}
