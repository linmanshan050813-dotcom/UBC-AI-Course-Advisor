package com.ubc.aiadvisor.dto;

import java.util.List;

/**
 * History Item DTO (DUT)
 * Data transfer object representing a single Q/A pair in history.
 * Used for API responses, converted from HistoryEntry entity.
 * Used in Phase 3+ of the AI Course Advisor project.
 */
public class HistoryItem {
    private String id;
    private String question;
    private String answer;
    private String timestamp;
    private List<String> citations;

    public HistoryItem() {
    }

    public HistoryItem(String id, String question, String answer, String timestamp) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    /**
     * Get history item identifier.
     * @return item ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set history item identifier.
     * @param id item ID
     */
    public void setId(String id) {
        this.id = id;
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
     * Get the timestamp of the interaction.
     * @return timestamp string
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp of the interaction.
     * @param timestamp timestamp string
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get list of citations associated with this answer.
     * @return list of citation strings
     */
    public List<String> getCitations() {
        return citations;
    }

    /**
     * Set list of citations associated with this answer.
     * @param citations list of citation strings
     */
    public void setCitations(List<String> citations) {
        this.citations = citations;
    }
}
