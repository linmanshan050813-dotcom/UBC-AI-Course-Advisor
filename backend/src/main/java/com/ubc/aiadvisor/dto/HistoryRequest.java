package com.ubc.aiadvisor.dto;

import java.util.List;

/**
 * History Request DTO (DUT)
 * Data transfer object for adding a new history entry.
 * Contains question, answer, and optional citations.
 * Used in Phase 3+ of the AI Course Advisor project.
 */
public class HistoryRequest {
    private String question;
    private String answer;
    private List<String> citations;

    public HistoryRequest() {
    }

    public HistoryRequest(String question, String answer, List<String> citations) {
        this.question = question;
        this.answer = answer;
        this.citations = citations;
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
     * Get optional list of citations.
     * @return list of citation strings
     */
    public List<String> getCitations() {
        return citations;
    }
    
    /**
     * Set optional list of citations.
     * @param citations list of citation strings
     */
    public void setCitations(List<String> citations) {
        this.citations = citations;
    }
}
