package com.ubc.aiadvisor.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Supabase Storage Service (DUT)
 * Handles file uploads, downloads, and deletions with Supabase Storage.
 * Uses OkHttp for direct REST API communication.
 * Used in Phase 4 of the AI Course Advisor project.
 */
@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.key}")
    private String serviceKey;

    @Value("${supabase.storage.bucket:course-documents}")
    private String bucketName;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Upload file to Supabase Storage.
     * Generates a unique filename using UUID.
     * 
     * @param file MultipartFile to upload
     * @return Unique filename of the uploaded object
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Build upload URL (encode bucket and filename)
        // String encodedBucket = URLEncoder.encode(bucketName, StandardCharsets.UTF_8); // REMOVED ENCODING
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s",
                supabaseUrl, bucketName, uniqueFilename); // MODIFIED

        // Create request body
        RequestBody requestBody = RequestBody.create(
                file.getBytes(),
                MediaType.parse(file.getContentType())
        );

        // Build request with authentication
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + serviceKey)
                .addHeader("Content-Type", file.getContentType())
                .build();

        // Execute request
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Failed to upload file to Supabase: " + errorBody);
            }
        }

        return uniqueFilename;
    }

    /**
     * Download file from Supabase Storage to temporary local file.
     * The temp file is marked for deletion on JVM exit.
     * 
     * @param filename Filename in Supabase Storage
     * @return Local temporary file
     * @throws IOException if download fails
     */
    public File downloadFile(String filename) throws IOException {
        // Build download URL
        // String encodedBucket = URLEncoder.encode(bucketName, StandardCharsets.UTF_8).replace("+", "%20"); // REMOVED ENCODING
        String downloadUrl = String.format("%s/storage/v1/object/%s/%s",
                supabaseUrl, bucketName, filename); // MODIFIED

        System.out.println("DEBUG: Downloading from " + downloadUrl);

        // Create request with authentication
        Request request = new Request.Builder()
                .url(downloadUrl)
                .get()
                .addHeader("Authorization", "Bearer " + serviceKey)
                .build();

        // Execute request and save to temp file
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                System.err.println("DEBUG: Supabase Download Error Body: " + errorBody);
                throw new IOException("Failed to download file from Supabase: " + response.code() + " " + response.message());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body from Supabase");
            }

            // Create temp file
            File tempFile = File.createTempFile("supabase-", "-" + filename);
            tempFile.deleteOnExit(); // Auto-delete when JVM exits

            // Write to temp file
            try (InputStream inputStream = body.byteStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        }
    }

    /**
     * Delete file from Supabase Storage.
     * 
     * @param filename Filename to delete
     * @throws IOException if deletion fails
     */
    public void deleteFile(String filename) throws IOException {
        // String encodedBucket = URLEncoder.encode(bucketName, StandardCharsets.UTF_8); // REMOVED ENCODING
        String deleteUrl = String.format("%s/storage/v1/object/%s/%s",
                supabaseUrl, bucketName, filename); // MODIFIED

        Request request = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .addHeader("Authorization", "Bearer " + serviceKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete file from Supabase: " + response.code());
            }
        }
    }
}
