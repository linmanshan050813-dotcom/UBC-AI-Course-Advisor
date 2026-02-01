package com.ubc.aiadvisor.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Mock AI Service (DUT)
 * Provides deterministic mock AI responses for fallback scenarios.
 * Thread-safe implementation using concurrent collections.
 * Used in Phase 4 of the AI Course Advisor project.
 */
@Service
public class MockAIService {

    // Thread-safe cache for question-to-answer mapping
    private final ConcurrentHashMap<String, AIEngineResult> responseCache = new ConcurrentHashMap<>();
    
    // Patterns for keyword matching (thread-safe - Pattern is immutable)
    private static final Pattern GRADING_PATTERN = Pattern.compile("(?i).*\\b(grading|grade|breakdown|percentage|weight|distribution)\\b.*");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("(?i).*\\b(deadline|due|date|submit|submission|late)\\b.*");
    private static final Pattern OFFICE_HOURS_PATTERN = Pattern.compile("(?i).*\\b(office\\s*hours?|office\\s*time|professor|instructor|help|questions?)\\b.*");
    private static final Pattern WORKLOAD_PATTERN = Pattern.compile("(?i).*\\b(workload|assignment|homework|lab|labs?|midterm|final|project)\\b.*");
    private static final Pattern PREREQUISITE_PATTERN = Pattern.compile("(?i).*\\b(prerequisite|requirement|required|need|background|knowledge)\\b.*");

    /**
     * Generate mock AI answer from user question.
     * 
     * @param question the user's question string
     * @return AIEngineResult containing answer text and citation strings
     */
    public AIEngineResult generateAnswer(String question) {
        // Validate inputs
        if (question == null || question.trim().isEmpty()) {
            return new AIEngineResult("Please ask course-related questions.", new ArrayList<>());
        }

        // Check cache first (thread-safe read)
        String cacheKey = question.toLowerCase().trim();
        AIEngineResult cached = responseCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // Generate answer based on keyword matching
        String answer;
        List<String> citations = new ArrayList<>();

        String lowerQuestion = question.toLowerCase();

        if (GRADING_PATTERN.matcher(lowerQuestion).matches()) {
            answer = "Based on the course syllabus, the grading breakdown is: Assignments 40%, Midterms 30%, Final Project 30%. A passing grade requires at least 60% overall.";
            citations.add("Grading Policy: Assignments 40%, Midterms 30%, Final Project 30%.");
        } else if (DEADLINE_PATTERN.matcher(lowerQuestion).matches()) {
            answer = "According to the course policy, late submissions are accepted with a 10% penalty per day. Please check the specific assignment deadlines in the course schedule.";
            citations.add("Late Policy: Late submissions are accepted with a 10% penalty per day.");
        } else if (OFFICE_HOURS_PATTERN.matcher(lowerQuestion).matches()) {
            answer = "Office hours are held every Tuesday and Thursday from 2-4 PM. The professor is available for questions about assignments and course material.";
            citations.add("Office Hours: Tuesday and Thursday from 2-4 PM.");
        } else if (WORKLOAD_PATTERN.matcher(lowerQuestion).matches()) {
            answer = "The course workload is moderate. There are weekly assignments, two midterms, and a final project. Students should expect regular programming labs and hands-on exercises.";
            citations.add("Workload: Weekly assignments, two midterms, and a final project.");
        } else if (PREREQUISITE_PATTERN.matcher(lowerQuestion).matches()) {
            answer = "Prerequisites include basic programming knowledge in Java or Python. The course is suitable for second-year computer science students.";
            citations.add("Prerequisites: Basic programming knowledge in Java or Python.");
        } else {
            answer = "Based on the course material, I found relevant information. For more specific details, please refer to the full syllabus.";
            citations.add("General course information.");
        }

        AIEngineResult result = new AIEngineResult(answer, citations);
        
        // Cache the result (thread-safe write)
        responseCache.put(cacheKey, result);
        
        return result;
    }

    /**
     * Clear the response cache.
     * Useful for testing state reset.
     */
    public void clearCache() {
        responseCache.clear();
    }

    /**
     * Get current number of cached responses.
     * @return cache size
     */
    public int getCacheSize() {
        return responseCache.size();
    }
}
