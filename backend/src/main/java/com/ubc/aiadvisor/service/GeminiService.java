package com.ubc.aiadvisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.io.FileInputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

/**
 * Gemini Service (DUT)
 * Handles direct communication with Google's Gemini API.
 * Manages request construction, MIME type handling, and response parsing.
 * Used in Phase 4 of the AI Course Advisor project.
 */
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor.
     * Initializes RestTemplate for HTTP requests.
     */
    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Constructor for testing.
     * Allows injection of mock RestTemplate.
     * 
     * @param restTemplate the RestTemplate to use
     */
    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Generate content using Gemini API with optional file attachment.
     * Supports PDF, TXT, and DOCX formats.
     * 
     * @param prompt the text prompt to send to Gemini
     * @param pdfFile the file to attach (optional, can be null)
     * @return the generated text response
     * @throws IOException if API call fails or file reading fails
     */
    public String generateContent(String prompt, File pdfFile) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API Key is missing");
        }

        // 1. Build JSON Payload
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode contentsNode = rootNode.putArray("contents");
        ObjectNode contentNode = contentsNode.addObject();
        ArrayNode partsNode = contentNode.putArray("parts");

        // Add Text Prompt
        partsNode.addObject().put("text", prompt);

        // Add File (if provided)
        if (pdfFile != null && pdfFile.exists()) {
            String mimeType = "application/pdf";
            String filename = pdfFile.getName().toLowerCase();
            String base64Content;

            if (filename.endsWith(".docx")) {
                // Extract text from DOCX using Apache POI
                try (FileInputStream fis = new FileInputStream(pdfFile);
                     XWPFDocument document = new XWPFDocument(fis);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    
                    String text = extractor.getText();
                    base64Content = Base64.getEncoder().encodeToString(text.getBytes());
                    mimeType = "text/plain";
                } catch (Exception e) {
                    System.err.println("Failed to extract text from DOCX: " + e.getMessage());
                    throw new IOException("Failed to extract text from DOCX", e);
                }
            } else {
                // For PDF and TXT, read raw bytes
                byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
                base64Content = Base64.getEncoder().encodeToString(fileContent);
                
                if (filename.endsWith(".txt")) {
                    mimeType = "text/plain";
                }
            }

            ObjectNode inlineDataNode = partsNode.addObject();
            ObjectNode inlineData = inlineDataNode.putObject("inline_data");
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Content);
        }

        // 2. Configure Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Make Request
        String urlWithKey = apiUrl + "?key=" + apiKey;
        HttpEntity<String> request = new HttpEntity<>(rootNode.toString(), headers);

        try {
            String response = restTemplate.postForObject(urlWithKey, request, String.class);
            return extractTextFromResponse(response);
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            System.err.println("Gemini API Error: " + e.getStatusCode() + " - " + body);
            String message = body != null && !body.isEmpty() ? body : "HTTP " + e.getStatusCode();
            throw new RuntimeException("Gemini API Error: " + message, e);
        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }

    /**
     * Parse text content from Gemini's JSON response structure.
     * 
     * @param jsonResponse raw JSON response
     * @return extracted text string
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(jsonResponse);
            return root.path("candidates").get(0)
                       .path("content").path("parts").get(0)
                       .path("text").asText();
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            // Return raw response if parsing fails to see what happened
            return jsonResponse;
        }
    }
}
