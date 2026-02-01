package com.ubc.aiadvisor.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Query Response DTO (DUT)
 * Data transfer object for AI query response body.
 * Contains AI-generated answer and list of citation references.
 * Used in Phase 2+ of the AI Course Advisor project.
 */
public class QueryResponse {
    private String answer;
    private List<Citation> citations;

    public QueryResponse() {
        this.citations = new ArrayList<>();
    }

    public QueryResponse(String answer, List<Citation> citations) {
        this.answer = answer;
        // Ensure citations list is never null
        this.citations = citations != null ? citations : new ArrayList<>();
    }

    /**
     * Get the AI answer.
     * @return answer string
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Set the AI answer.
     * @param answer answer string
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Get the list of citations.
     * @return list of Citation objects
     */
    public List<Citation> getCitations() {
        return citations;
    }

    /**
     * Set the list of citations.
     * Ensures the list is never null (defaults to empty list).
     * @param citations list of Citation objects
     */
    public void setCitations(List<Citation> citations) {
        // Ensure citations list is never null
        this.citations = citations != null ? citations : new ArrayList<>();
    }
}
