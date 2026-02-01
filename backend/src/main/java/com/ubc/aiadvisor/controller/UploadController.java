package com.ubc.aiadvisor.controller;

import com.ubc.aiadvisor.security.JwtUtil;
import com.ubc.aiadvisor.service.UploadService;
import com.ubc.aiadvisor.model.UploadedDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Upload Controller (DUT)
 * Handles course document file upload requests.
 * Validates file type and size, processes upload, and returns extracted data.
 * Used in Phase 1 of the AI Course Advisor project.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class UploadController {

    private static final long MAX_FILE_SIZE_MB = 1;

    private final UploadService uploadService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UploadController(UploadService uploadService, JwtUtil jwtUtil) {
        this.uploadService = uploadService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Process file upload request.
     * POST /api/upload
     * 
     * @param file multipart file containing course document (PDF or TXT)
     * @param token Authorization header with JWT
     * @return response map with upload success status and document ID
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return buildErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Role-based access: only professors may upload syllabi
        String role = jwtUtil.getUserRoleFromToken(token);
        if (role != null && role.equalsIgnoreCase("student")) {
            return buildErrorResponse("Forbidden: professor role required", HttpStatus.FORBIDDEN);
        }

        // Validate file is present and not empty
        if (file == null || file.isEmpty()) {
            return buildErrorResponse("No file provided", HttpStatus.BAD_REQUEST);
        }

        // Validate file extension is PDF or TXT
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !uploadService.isValidFileType(originalFilename)) {
            return buildErrorResponse(
                "Invalid file type. Only PDF and TXT files are allowed.",
                HttpStatus.BAD_REQUEST
            );
        }

        // Validate file size does not exceed maximum limit
        if (!uploadService.isValidFileSize(file.getSize(), MAX_FILE_SIZE_MB)) {
            return buildErrorResponse(
                String.format("File size exceeds maximum limit of %d MB", MAX_FILE_SIZE_MB),
                HttpStatus.PAYLOAD_TOO_LARGE
            );
        }

        // Process file upload
        try {
            Map<String, Object> result = uploadService.processUpload(file, userId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return buildErrorResponse(
                "Failed to save file: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            return buildErrorResponse(
                "Upload failed: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Get user's uploads.
     * GET /api/uploads
     * 
     * @param token Authorization header with JWT
     * @return list of uploaded documents
     */
    @GetMapping("/uploads")
    public ResponseEntity<?> getUserUploads(@RequestHeader(value = "Authorization", required = false) String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return buildErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        
        List<UploadedDoc> uploads = uploadService.getUserUploads(userId);
        return ResponseEntity.ok(uploads);
    }

    /**
     * Delete a specific uploaded document.
     * DELETE /api/upload/{id}
     * 
     * @param id ID of the document to delete
     * @param token Authorization header with JWT
     * @return 200 OK on success
     */
    @DeleteMapping("/upload/{id}")
    public ResponseEntity<?> deleteUpload(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return buildErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        String role = jwtUtil.getUserRoleFromToken(token);
        if (role != null && role.equalsIgnoreCase("student")) {
            return buildErrorResponse("Forbidden: professor role required", HttpStatus.FORBIDDEN);
        }
        
        try {
            uploadService.deleteUpload(id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException iae) {
            return buildErrorResponse("Forbidden: cannot modify another user's document", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return buildErrorResponse("Failed to delete upload: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Clear all uploaded documents for the user.
     * DELETE /api/uploads
     * 
     * @param token Authorization header with JWT
     * @return 200 OK on success
     */
    @DeleteMapping("/uploads")
    public ResponseEntity<?> clearUploads(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return buildErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        String role = jwtUtil.getUserRoleFromToken(token);
        if (role != null && role.equalsIgnoreCase("student")) {
            return buildErrorResponse("Forbidden: professor role required", HttpStatus.FORBIDDEN);
        }
        
        try {
            uploadService.clearUploads(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return buildErrorResponse("Failed to clear uploads: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
