package com.example.demo.controller;

import com.example.demo.dto.QueryRequest;
import com.example.demo.dto.QueryResponse;
import com.example.demo.dto.DocumentUploadResponse;
import com.example.demo.service.AIService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI Controller - handles all AI-related requests from the frontend
 * Acts as a proxy/gateway to the AI Model service
 * All endpoints are protected by FirebaseTokenFilter
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * POST /api/ai/ask
     * Ask a question and get an AI-generated answer using RAG
     * 
     * Request body:
     * {
     *   "question": "What is the capital of France?"
     * }
     * 
     * Response:
     * {
     *   "answer": "The capital of France is Paris.",
     *   "success": true
     * }
     */
    @PostMapping("/ask")
    public ResponseEntity<QueryResponse> askQuestion(
            @RequestBody QueryRequest request,
            HttpServletRequest httpRequest) {
        
        // Get authenticated user ID from request (set by FirebaseTokenFilter)
        String userId = (String) httpRequest.getAttribute("uid");
        
        System.out.println("📝 Question from user " + userId + ": " + request.getQuestion());
        
        // Forward to AI service
        QueryResponse response = aiService.askQuestion(request.getQuestion(), userId);
        
        if (response.isSuccess()) {
            System.out.println("✅ Answer generated successfully");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("❌ Failed to generate answer: " + response.getError());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/ai/document/upload
     * Upload a document (PDF or TXT) to be processed by the AI
     * 
     * Form data:
     * - file: the PDF or TXT file
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Document uploaded successfully",
     *   "documentId": "doc123"
     * }
     */
    @PostMapping("/document/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        
        // Get authenticated user ID
        String userId = (String) httpRequest.getAttribute("uid");
        
        System.out.println("📄 Document upload from user " + userId + ": " + file.getOriginalFilename());
        
        // Forward to AI service
        DocumentUploadResponse response = aiService.uploadDocument(file, userId);
        
        if (response.isSuccess()) {
            System.out.println("✅ Document uploaded successfully");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("❌ Failed to upload document: " + response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * GET /api/ai/documents
     * Get list of all documents uploaded by the authenticated user
     * 
     * Response: JSON array of documents
     */
    @GetMapping("/documents")
    public ResponseEntity<String> getUserDocuments(HttpServletRequest httpRequest) {
        
        // Get authenticated user ID
        String userId = (String) httpRequest.getAttribute("uid");
        
        System.out.println("📚 Fetching documents for user " + userId);
        
        // Forward to AI service
        String documents = aiService.getUserDocuments(userId);
        
        return ResponseEntity.ok(documents);
    }

    /**
     * DELETE /api/ai/document/{documentId}
     * Delete a specific document
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Document deleted"
     * }
     */
    @DeleteMapping("/document/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable String documentId,
            HttpServletRequest httpRequest) {
        
        // Get authenticated user ID
        String userId = (String) httpRequest.getAttribute("uid");
        
        System.out.println("🗑️ Deleting document " + documentId + " for user " + userId);
        
        // Forward to AI service
        boolean success = aiService.deleteDocument(documentId, userId);
        
        if (success) {
            return ResponseEntity.ok("{\"success\": true, \"message\": \"Document deleted\"}");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Failed to delete document\"}");
        }
    }
}
