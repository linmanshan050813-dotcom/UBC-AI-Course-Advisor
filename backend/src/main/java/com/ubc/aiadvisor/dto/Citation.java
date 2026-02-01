package com.ubc.aiadvisor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Citation DTO (DUT)
 * Data transfer object representing a citation reference in query response.
 * Maps citation ID to citation text content.
 * API response uses "content" field, internal uses "text" field.
 * Used in Phase 2+ of the AI Course Advisor project.
 */
public class Citation {
    private String id;
    private String text;

    public Citation() {
    }

    public Citation(String id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Get citation identifier.
     * @return citation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set citation identifier.
     * @param id citation ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get citation text content.
     * @return text content
     */
    public String getText() {
        return text;
    }

    /**
     * Set citation text content.
     * @param text text content
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * JSON property getter for API compatibility.
     * Maps internal "text" field to API "content" field.
     * 
     * @return citation text content
     */
    @JsonProperty("content")
    public String getContent() {
        return text;
    }
    
    /**
     * JSON property setter for API compatibility.
     * Maps API "content" field to internal "text" field.
     * 
     * @param content citation text content
     */
    @JsonProperty("content")
    public void setContent(String content) {
        this.text = content;
    }
}
