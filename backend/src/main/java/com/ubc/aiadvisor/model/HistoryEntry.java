package com.ubc.aiadvisor.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * History Entry Entity (DUT)
 * JPA entity representing a Q/A history entry in the database.
 * Stores question, answer, and timestamp for history tracking.
 * Used in Phase 3 of the AI Course Advisor project.
 */
@Entity
@Table(name = "history_entries")
public class HistoryEntry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String citations;

    @Column(nullable = false, name = "user_id", length = 64)
    private String userId;

    @Column(nullable = false, name = "created_at")
    @JsonIgnore
    private LocalDateTime createdAt;

    public HistoryEntry() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public HistoryEntry(String question, String answer, String citations, String userId) {
        this.id = UUID.randomUUID().toString();
        this.question = question;
        this.answer = answer;
        this.citations = citations;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Get entry unique identifier.
     * @return entry ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set entry unique identifier.
     * @param id entry ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * JSON getter for frontend compatibility.
     * Returns ID as string for JSON serialization.
     * 
     * @return ID string or empty string if null
     */
    @JsonGetter("id")
    public String getIdAsString() {
        return id != null ? id : "";
    }

    /**
     * Get the question text.
     * @return question string
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Set the question text.
     * @param question question string
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Get the AI answer text.
     * @return answer string
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Set the AI answer text.
     * @param answer answer string
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Get serialized citations.
     * @return citations JSON string
     */
    public String getCitations() {
        return citations;
    }

    /**
     * Set serialized citations.
     * @param citations citations JSON string
     */
    public void setCitations(String citations) {
        this.citations = citations;
    }

    /**
     * Get ID of user who made the query.
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set ID of user who made the query.
     * @param userId user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get timestamp when entry was created.
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Set timestamp when entry was created.
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * JSON getter for frontend compatibility.
     * Returns creation timestamp as formatted string.
     * 
     * @return formatted timestamp string or empty string if null
     */
    @JsonGetter("timestamp")
    public String getTimestampAsString() {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }
}
