package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.UploadedDoc;
import com.ubc.aiadvisor.repository.UploadedDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Upload Service (DUT)
 * Handles file upload, storage, and metadata persistence.
 * Integrates with Supabase Storage and validates files via AIEngine.
 * Used in Phase 4 of the AI Course Advisor project.
 */
@Service
public class UploadService {

    private final SupabaseStorageService supabaseStorageService;
    private final UploadedDocRepository uploadedDocRepository;
    private final AIEngine aiEngine;

    @Autowired
    public UploadService(SupabaseStorageService supabaseStorageService, 
                         UploadedDocRepository uploadedDocRepository, 
                         AIEngine aiEngine) {
        this.supabaseStorageService = supabaseStorageService;
        this.uploadedDocRepository = uploadedDocRepository;
        this.aiEngine = aiEngine;
    }

    /**
     * Process file upload: upload to storage, validate, and save metadata.
     * 
     * @param file the multipart file to upload
     * @param userId the user ID of the uploader
     * @return map containing upload results
     * @throws IOException if upload or validation fails
     */
    public Map<String, Object> processUpload(MultipartFile file, String userId) throws IOException {
        // Upload to Supabase Storage
        String supabaseFilename = supabaseStorageService.uploadFile(file);
        
        // Download temp copy for AI processing
        File tempFile = supabaseStorageService.downloadFile(supabaseFilename);
        
        // Extract course name using AI
        String courseName = "Unknown Course";
        if (isValidFileType(file.getOriginalFilename())) {
            try {
                courseName = aiEngine.extractCourseName(tempFile);
                
                // Check if it's a valid syllabus (fuzzy match)
                if (courseName != null && courseName.toLowerCase().contains("not a syllabus")) {
                    // Cleanup and reject
                    supabaseStorageService.deleteFile(supabaseFilename);
                    throw new IllegalArgumentException("The uploaded document does not appear to be a course syllabus.");
                }
            } catch (Exception e) {
                // If extraction fails (e.g. API error), we log but maybe allow?
                // Or rethrow if it was our rejection exception
                if (e instanceof IllegalArgumentException) {
                    throw (IOException) new IOException(e.getMessage()).initCause(e);
                }
                System.err.println("Failed to extract course name: " + e.getMessage());
            }
        }
        
        // Temp file will be auto-deleted on exit (deleteOnExit set in downloadFile)
        
        // Replacement logic: remove existing index for same course (stability for R-U-04 / S-03)
        if (courseName != null && !courseName.isBlank()) {
            List<UploadedDoc> existing = uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(userId);
            for (UploadedDoc prev : existing) {
                if (courseName.equalsIgnoreCase(prev.getCourseName())) {
                    try {
                        supabaseStorageService.deleteFile(prev.getFilename());
                    } catch (Exception e) {
                        System.err.println("Warning: Failed to delete previous course file: " + e.getMessage());
                    }
                    uploadedDocRepository.delete(prev);
                }
            }
        }

        // Save metadata to database (new index active)
        UploadedDoc doc = new UploadedDoc(supabaseFilename, file.getOriginalFilename(), userId);
        doc.setCourseName(courseName);
        uploadedDocRepository.save(doc);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("filename", supabaseFilename);
        response.put("originalFilename", file.getOriginalFilename());
        response.put("docId", doc.getId());
        response.put("courseName", courseName);

        return response;
    }
    
    /**
     * Retrieve all uploads for a specific user.
     * 
     * @param userId user ID
     * @return list of uploaded documents
     */
    public List<UploadedDoc> getUserUploads(String userId) {
        return uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(userId);
    }
    
    /**
     * Retrieve specific upload by ID.
     * 
     * @param docId document ID
     * @return UploadedDoc or null if not found
     */
    public UploadedDoc getUploadById(String docId) {
        return uploadedDocRepository.findById(docId).orElse(null);
    }

    /**
     * Validate file type based on extension.
     * Allowed: PDF, TXT, DOCX.
     * 
     * @param filename filename to check
     * @return true if valid
     */
    public boolean isValidFileType(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".txt") || lower.endsWith(".docx");
    }

    /**
     * Validate file size against maximum limit.
     * 
     * @param fileSize size in bytes
     * @param maxSizeMB max size in megabytes
     * @return true if within limit
     */
    public boolean isValidFileSize(long fileSize, long maxSizeMB) {
        return fileSize <= maxSizeMB * 1024 * 1024;
    }

    /**
     * Delete a specific upload.
     * Removes file from Supabase and metadata from DB.
     * 
     * @param docId document ID
     * @param userId user ID requesting deletion (for ownership check)
     * @throws IOException if storage deletion fails
     * @throws IllegalArgumentException if document not found or unauthorized
     */
    public void deleteUpload(String docId, String userId) throws IOException {
        UploadedDoc doc = uploadedDocRepository.findById(docId).orElse(null);
        if (doc != null && doc.getUserId().equals(userId)) {
            // Delete from Supabase
            try {
                supabaseStorageService.deleteFile(doc.getFilename());
            } catch (Exception e) {
                System.err.println("Warning: Failed to delete file from Supabase: " + e.getMessage());
                // Continue to delete from DB even if storage delete fails (orphaned file better than broken DB record)
            }
            uploadedDocRepository.delete(doc);
        } else {
            throw new IllegalArgumentException("Document not found or unauthorized");
        }
    }

    /**
     * Clear all uploads for a user.
     * 
     * @param userId user ID
     */
    public void clearUploads(String userId) {
        List<UploadedDoc> uploads = uploadedDocRepository.findByUserIdOrderByUploadedAtDesc(userId);
        for (UploadedDoc doc : uploads) {
            try {
                supabaseStorageService.deleteFile(doc.getFilename());
            } catch (Exception e) {
                System.err.println("Warning: Failed to delete file from Supabase: " + e.getMessage());
            }
            uploadedDocRepository.delete(doc);
        }
    }
}
