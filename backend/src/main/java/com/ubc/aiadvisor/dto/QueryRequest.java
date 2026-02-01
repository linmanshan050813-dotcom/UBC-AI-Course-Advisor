package com.ubc.aiadvisor.dto;

/**
 * Query Request DTO (DUT)
 * Data transfer object for AI query request body.
 * Contains the user's question string and optional document ID.
 * Used in Phase 2+ of the AI Course Advisor project.
 */
public class QueryRequest {
    private String question;
    private String docId;

    public QueryRequest() {
    }

    public QueryRequest(String question) {
        this.question = question;
    }
    
    public QueryRequest(String question, String docId) {
        this.question = question;
        this.docId = docId;
    }

    /**
     * Get the user's question.
     * @return question string
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Set the user's question.
     * @param question question string
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    
    /**
     * Get optional document ID for context.
     * @return document ID
     */
    public String getDocId() {
        return docId;
    }
    
    /**
     * Set optional document ID for context.
     * @param docId document ID
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }
}
