package com.example.demo.service;

import com.example.demo.dto.QueryRequest;
import com.example.demo.dto.QueryResponse;
import com.example.demo.dto.DocumentUploadResponse;
import com.example.demo.factory.ResponseFactory;
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
            // Prepare request - AI Model expects {id, question}
            QueryRequest request = new QueryRequest(question, userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<QueryRequest> entity = new HttpEntity<>(request, headers);
            
            // Call AI Model service - returns {id, reply}
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/query",
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // Parse response - AI Model returns {"id": "...", "reply": "..."}
            String responseBody = response.getBody();
            if (responseBody != null && responseBody.contains("\"reply\"")) {
                // Extract reply value using simple string manipulation
                int replyStart = responseBody.indexOf("\"reply\"");
                if (replyStart != -1) {
                    int valueStart = responseBody.indexOf(":", replyStart) + 1;
                    valueStart = responseBody.indexOf("\"", valueStart) + 1; // Skip opening quote
                    
                    // Find the closing quote, handling escaped quotes
                    int valueEnd = valueStart;
                    while (valueEnd < responseBody.length()) {
                        if (responseBody.charAt(valueEnd) == '"' && responseBody.charAt(valueEnd - 1) != '\\') {
                            break;
                        }
                        valueEnd++;
                    }
                    
                    if (valueEnd < responseBody.length()) {
                        String reply = responseBody.substring(valueStart, valueEnd);
                        // Unescape common JSON escapes
                        reply = reply.replace("\\\"", "\"")
                                    .replace("\\n", "\n")
                                    .replace("\\t", "\t")
                                    .replace("\\\\", "\\");
                        return ResponseFactory.createSuccessQueryResponse(reply);
                    }
                }
            }
            
            return ResponseFactory.createSuccessQueryResponse(responseBody);
            
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            return ResponseFactory.createAIServiceErrorResponse(e);
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
            // Validate file type - AI Model accepts .txt, .md, .pdf
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".pdf") && !filename.endsWith(".txt") && !filename.endsWith(".md"))) {
                return ResponseFactory.createUnsupportedFileTypeResponse();
            }

            // Prepare multipart request - AI Model expects "file" and "id" parameters
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("id", userId);  // AI Model expects "id" not "userId"

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call AI Model service - endpoint is /api/fileUpload
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/fileUpload",
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            // AI Model returns {filename, fileSize}
            String responseBody = response.getBody();
            if (responseBody != null && responseBody.contains("filename")) {
                return ResponseFactory.createSuccessUploadResponse(userId);
            }

            return ResponseFactory.createSuccessUploadResponse();

        } catch (Exception e) {
            System.err.println("Error uploading document to AI service: " + e.getMessage());
            return ResponseFactory.createUploadExceptionResponse(e);
        }
    }

    /**
     * Get list of user's documents from AI Model
     * @param userId The authenticated user's ID
     * @return List of documents
     */
    public String getUserDocuments(String userId) {
        try {
          
            // AI Model expects DELETE /api/deleteDB with body {id, question}
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request body with id
            String requestBody = "{\"id\": \"" + userId + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/documents",
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // Check if response contains "deleted" status
            String responseBody = response.getBody();
            return responseBody;

            
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
            // AI Model expects DELETE /api/deleteDB with body {id, question}
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request body with id
            String requestBody = "{\"id\": \"" + userId + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/deleteDB",
                HttpMethod.DELETE,
                entity,
                String.class
            );
            
            // Check if response contains "deleted" status
            String responseBody = response.getBody();
            return responseBody != null && responseBody.contains("deleted");
            
        } catch (Exception e) {
            System.err.println("Error deleting document: " + e.getMessage());
            return false;
        }
    }
}
