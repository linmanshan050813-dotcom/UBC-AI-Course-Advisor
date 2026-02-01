package com.ubc.aiadvisor.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Uploaded Document Entity (DUT)
 * JPA entity representing a course document file upload in the database.
 * Stores metadata including filename, upload timestamp, and ownership.
 * Used in Phase 1+ of the AI Course Advisor project.
 */
@Entity
@Table(name = "uploaded_docs")
public class UploadedDoc {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 255)
    private String originalFilename;
    
    @Column(length = 255)
    private String courseName;

    @Column(nullable = false, name = "user_id", length = 64)
    private String userId;

    @Column(nullable = false, name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public UploadedDoc() {
        this.id = UUID.randomUUID().toString();
        this.uploadedAt = LocalDateTime.now();
    }

    public UploadedDoc(String filename, String originalFilename, String userId) {
        this.id = UUID.randomUUID().toString();
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.userId = userId;
        this.uploadedAt = LocalDateTime.now();
    }

    /**
     * Get document unique identifier.
     * @return document ID
     */
    public String getId() { return id; }
    
    /**
     * Set document unique identifier.
     * @param id document ID
     */
    public void setId(String id) { this.id = id; }

    /**
     * Get stored filename.
     * @return filename
     */
    public String getFilename() { return filename; }
    
    /**
     * Set stored filename.
     * @param filename filename
     */
    public void setFilename(String filename) { this.filename = filename; }

    /**
     * Get original uploaded filename.
     * @return original filename
     */
    public String getOriginalFilename() { return originalFilename; }
    
    /**
     * Set original uploaded filename.
     * @param originalFilename original filename
     */
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    /**
     * Get course name associated with document.
     * @return course name
     */
    public String getCourseName() { return courseName; }
    
    /**
     * Set course name associated with document.
     * @param courseName course name
     */
    public void setCourseName(String courseName) { this.courseName = courseName; }

    /**
     * Get ID of user who uploaded document.
     * @return user ID
     */
    public String getUserId() { return userId; }
    
    /**
     * Set ID of user who uploaded document.
     * @param userId user ID
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Get timestamp when document was uploaded.
     * @return upload timestamp
     */
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    
    /**
     * Set timestamp when document was uploaded.
     * @param uploadedAt upload timestamp
     */
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    /**
     * JSON getter for frontend compatibility.
     * Returns upload timestamp as formatted string.
     * 
     * @return formatted timestamp string or empty string
     */
    @JsonGetter("uploadedAt")
    public String getUploadedAtAsString() {
        return uploadedAt != null ? uploadedAt.format(FORMATTER) : "";
    }
}
