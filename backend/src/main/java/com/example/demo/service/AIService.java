package com.example.demo.service;

import com.example.demo.dto.QueryRequest;
import com.example.demo.dto.QueryResponse;
import com.example.demo.dto.DocumentUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    
    // AI Model service URL - will be configurable
    @Value("${ai.service.url:http://localhost:9000}")
    private String aiServiceUrl;

    public AIService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send a query to the AI Model and get an answer
     * @param question The user's question
     * @param userId The authenticated user's ID
     * @return QueryResponse with the AI's answer
     */
    public QueryResponse askQuestion(String question, String userId) {
        try {
            // Prepare request
            QueryRequest request = new QueryRequest(question, userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<QueryRequest> entity = new HttpEntity<>(request, headers);
            
            // Call AI Model service
            ResponseEntity<QueryResponse> response = restTemplate.exchange(
                aiServiceUrl + "/api/query",
                HttpMethod.POST,
                entity,
                QueryResponse.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            return new QueryResponse(null, false, "Failed to get answer from AI service: " + e.getMessage());
        }
    }

    /**
     * Upload a document (PDF or TXT) to the AI Model for processing
     * @param file The document file
     * @param userId The authenticated user's ID
     * @return DocumentUploadResponse with success status
     */
    public DocumentUploadResponse uploadDocument(MultipartFile file, String userId) {
        try {
            // Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".pdf") && !filename.endsWith(".txt"))) {
                return new DocumentUploadResponse(false, "Only PDF and TXT files are supported");
            }

            // Prepare multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("userId", userId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call AI Model service
            ResponseEntity<DocumentUploadResponse> response = restTemplate.exchange(
                aiServiceUrl + "/api/document/upload",
                HttpMethod.POST,
                requestEntity,
                DocumentUploadResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            System.err.println("Error uploading document to AI service: " + e.getMessage());
            return new DocumentUploadResponse(false, "Failed to upload document: " + e.getMessage());
        }
    }

    /**
     * Get list of user's documents from AI Model
     * @param userId The authenticated user's ID
     * @return List of documents
     */
    public String getUserDocuments(String userId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                aiServiceUrl + "/api/documents?userId=" + userId,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error getting user documents: " + e.getMessage());
            return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Delete a document from the AI Model
     * @param documentId The document ID to delete
     * @param userId The authenticated user's ID
     * @return Success status
     */
    public boolean deleteDocument(String documentId, String userId) {
        try {
            restTemplate.delete(
                aiServiceUrl + "/api/document/" + documentId + "?userId=" + userId
            );
            return true;
            
        } catch (Exception e) {
            System.err.println("Error deleting document: " + e.getMessage());
            return false;
        }
    }
}
