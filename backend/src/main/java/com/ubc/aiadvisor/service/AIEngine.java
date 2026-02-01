package com.ubc.aiadvisor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI Engine Service (DUT)
 * Integrates with Gemini API to generate answers from course content.
 * Sends the PDF directly to Gemini for analysis.
 * Thread-safe implementation.
 */
@Service
public class AIEngine {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Performance metrics (thread-safe)
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    @Autowired
    public AIEngine(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * Generate AI answer from user question and PDF content.
     * 
     * @param question the user's question string
     * @param pdfFile the course syllabus PDF file
     * @return AIEngineResult containing answer text and formatted citations
     */
    public AIEngineResult generateAnswer(String question, File pdfFile) {
        totalRequests.incrementAndGet();
        
        if (question == null || question.trim().isEmpty()) {
            return new AIEngineResult("Please ask questions related to the course", new ArrayList<>());
        }

        if (pdfFile == null || !pdfFile.exists()) {
            return new AIEngineResult("No course document found. Please upload a syllabus first.", new ArrayList<>());
        }

        try {
            String prompt = buildPrompt(question);
            String response = geminiService.generateContent(prompt, pdfFile);
            AIEngineResult result = parseGeminiResponse(response);
            successCount.incrementAndGet();
            return result;
        } catch (Exception e) {
            errorCount.incrementAndGet();
            System.err.println("AIEngine Error: " + e.getMessage());
            e.printStackTrace();
            // Return detailed error for debugging (temporary)
            return new AIEngineResult("Error: " + e.getMessage(), new ArrayList<>());
        }
    }
    
    /**
     * Extract course name from the PDF file.
     * 
     * @param pdfFile the course syllabus PDF file
     * @return the extracted course name or "Unknown Course" if failed
     */
    public String extractCourseName(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            return "Unknown Course";
        }
        
        try {
            String prompt = "Analyze the attached syllabus and identify the Course Name and Code (e.g., 'CPEN 221: Software Construction'). " +
                            "Return ONLY the course name as a plain string. Do not include any other text or JSON. If the attached file does not appear to be a syllabus, return the string 'Not a Syllabus!'";
            String response = geminiService.generateContent(prompt, pdfFile);
            
            // Clean up response
            String courseName = response.trim();
            if (courseName.startsWith("```")) {
                // Strip markdown if present
                courseName = courseName.replaceAll("```.*", "").trim();
            }
            // If response is too long, it might be hallucinating, truncate it
            if (courseName.length() > 100) {
                courseName = courseName.substring(0, 100) + "...";
            }
            return courseName;
        } catch (Exception e) {
            System.err.println("Failed to extract course name: " + e.getMessage());
            return "Unknown Course";
        }
    }

    /**
     * Build the AI prompt instructions.
     * 
     * @param question user question
     * @return full prompt string
     */
    private String buildPrompt(String question) {
        return "You are an AI Course Advisor. Analyze the attached PDF course syllabus to answer the user's question.\n\n" +
               "User Question: \"" + question + "\"\n\n" +
               "INSTRUCTIONS:\n" +
               "1. Check if the question is relevant to the course discussed in the syllabus. If not, respond with JSON: {\"answer\": \"Please ask questions related to the course\", \"citations\": []}\n" +
               "2. If relevant, look for the answer in the PDF. If the answer is found, provide a concise and helpful answer.\n" +
               "3. If the answer is NOT found in the syllabus, respond with JSON: {\"answer\": \"Sorry, this information does not appear to be provided in the syllabus.\", \"citations\": []}\n" +
               "4. If you provide an answer, you MUST provide a citation. The citation should include:\n" +
               "   - A direct quote or concise summary (max 200 chars).\n" +
               "   - The Page Number where it was found.\n" +
               "   - The approximate location on the page (Top, Middle, or Bottom).\n" +
               "   Format: \"[Quote/Summary] (Page X, [Location])\"\n" +
               "5. Output MUST be valid JSON in the following format:\n" +
               "{\n" +
               "  \"answer\": \"Your answer here\",\n" +
               "  \"citations\": [\"[Quote] (Page 1, Top)\"]\n" +
               "}\n" +
               "7. Provide ONLY the JSON. Do not include markdown formatting like ```json.";
    }

    /**
     * Parse the JSON response from Gemini.
     * 
     * @param response raw JSON string from API
     * @return parsed AIEngineResult object
     */
    private AIEngineResult parseGeminiResponse(String response) {
        try {
            // Clean up response if it contains markdown code blocks
            String jsonStr = response.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            JsonNode root = objectMapper.readTree(jsonStr);
            String answer = root.path("answer").asText("No answer provided.");
            List<String> citations = new ArrayList<>();
            
            JsonNode citationsNode = root.path("citations");
            if (citationsNode.isArray()) {
                for (JsonNode node : citationsNode) {
                    citations.add(node.asText());
                }
            }
            
            return new AIEngineResult(answer, citations); 

        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            // Fallback for simple text response if JSON parsing fails
            return new AIEngineResult(response, new ArrayList<>());
        }
    }
    
    /**
     * Get performance metrics (thread-safe).
     * 
     * @return PerformanceMetrics object with current statistics
     */
    public PerformanceMetrics getPerformanceMetrics() {
        return new PerformanceMetrics(
            totalRequests.get(),
            successCount.get(),
            errorCount.get()
        );
    }
    
    /**
     * Performance metrics class for monitoring.
     * Immutable snapshot of engine statistics.
     */
    public static class PerformanceMetrics {
        private final long totalRequests;
        private final long successCount;
        private final long errorCount;
        
        /**
         * Create metrics snapshot.
         * 
         * @param totalRequests total requests processed
         * @param successCount total successful requests
         * @param errorCount total failed requests
         */
        public PerformanceMetrics(long totalRequests, long successCount, long errorCount) {
            this.totalRequests = totalRequests;
            this.successCount = successCount;
            this.errorCount = errorCount;
        }
        
        /** @return total number of requests */
        public long getTotalRequests() { return totalRequests; }
        
        /** @return total number of successful requests */
        public long getSuccessCount() { return successCount; }
        
        /** @return total number of failed requests */
        public long getErrorCount() { return errorCount; }
        
        // Backwards compatibility stubs for HealthController
        public long getTimeoutCount() { return 0; }
        public long getFallbackCount() { return errorCount; }
        
        /** @return success rate as a percentage (0.0 to 1.0) */
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successCount / totalRequests : 0.0;
        }
        
        public double getTimeoutRate() {
            return 0.0;
        }
        
        public double getFallbackRate() {
            return totalRequests > 0 ? (double) errorCount / totalRequests : 0.0;
        }
    }
}
