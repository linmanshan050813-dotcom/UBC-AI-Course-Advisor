package com.ubc.aiadvisor.service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Engine Result (DUT)
 * Result object from AIEngine containing generated answer and text citations.
 * Citations are now direct text strings as formatted by Gemini.
 * Used in Phase 4 of the AI Course Advisor project.
 */
public class AIEngineResult {
    private String answer;
    private List<String> citations;

    public AIEngineResult() {
        this.citations = new ArrayList<>();
    }

    public AIEngineResult(String answer, List<String> citations) {
        this.answer = answer;
        // Ensure citations list is never null
        this.citations = citations != null ? citations : new ArrayList<>();
    }

    /**
     * Get the generated answer text.
     * @return answer string
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Set the generated answer text.
     * @param answer answer string
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Get list of extracted citations.
     * @return list of citation strings
     */
    public List<String> getCitations() {
        return citations;
    }

    /**
     * Set list of extracted citations.
     * Ensures list is never null.
     * @param citations list of citation strings
     */
    public void setCitations(List<String> citations) {
        // Ensure citations list is never null
        this.citations = citations != null ? citations : new ArrayList<>();
    }
}
