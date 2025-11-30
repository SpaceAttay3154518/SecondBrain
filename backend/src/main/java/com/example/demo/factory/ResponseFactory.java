package com.example.demo.factory;

import com.example.demo.dto.QueryResponse;
import com.example.demo.dto.DocumentUploadResponse;

/**
 * Factory Pattern implementation for creating response objects.
 * Centralizes response creation logic and provides clear, semantic methods
 * for different response scenarios.
 */
public class ResponseFactory {

    // ===== Query Response Factory Methods =====

    /**
     * Create a successful query response with an answer
     * @param answer The AI-generated answer
     * @return QueryResponse with success status
     */
    public static QueryResponse createSuccessQueryResponse(String answer) {
        return new QueryResponse(answer, true);
    }

    /**
     * Create a failed query response with error details
     * @param errorMessage The error message to include
     * @return QueryResponse with failure status and error
     */
    public static QueryResponse createErrorQueryResponse(String errorMessage) {
        return new QueryResponse(null, false, errorMessage);
    }

    /**
     * Create a query response with custom answer and error (for edge cases)
     * @param answer The answer (may be partial or fallback)
     * @param errorMessage Additional error context
     * @return QueryResponse with both answer and error
     */
    public static QueryResponse createPartialQueryResponse(String answer, String errorMessage) {
        return new QueryResponse(answer, false, errorMessage);
    }

    // ===== Document Upload Response Factory Methods =====

    /**
     * Create a successful document upload response
     * @param documentId The ID of the uploaded document
     * @return DocumentUploadResponse with success status
     */
    public static DocumentUploadResponse createSuccessUploadResponse(String documentId) {
        return new DocumentUploadResponse(true, "Document uploaded successfully", documentId);
    }

    /**
     * Create a successful document upload response without document ID
     * @return DocumentUploadResponse with success status
     */
    public static DocumentUploadResponse createSuccessUploadResponse() {
        return new DocumentUploadResponse(true, "Document uploaded successfully");
    }

    /**
     * Create a failed document upload response with error details
     * @param errorMessage The error message explaining the failure
     * @return DocumentUploadResponse with failure status
     */
    public static DocumentUploadResponse createErrorUploadResponse(String errorMessage) {
        return new DocumentUploadResponse(false, errorMessage);
    }

    /**
     * Create a validation error response for unsupported file types
     * @return DocumentUploadResponse with validation error
     */
    public static DocumentUploadResponse createUnsupportedFileTypeResponse() {
        return new DocumentUploadResponse(false, "Only PDF, TXT, and MD files are supported");
    }

    /**
     * Create a generic document upload error response
     * @param exception The exception that caused the failure
     * @return DocumentUploadResponse with error message from exception
     */
    public static DocumentUploadResponse createUploadExceptionResponse(Exception exception) {
        return new DocumentUploadResponse(false, "Failed to upload document: " + exception.getMessage());
    }

    // ===== AI Service Error Response Factory Methods =====

    /**
     * Create a query error response for AI service failures
     * @param exception The exception from the AI service call
     * @return QueryResponse with formatted error message
     */
    public static QueryResponse createAIServiceErrorResponse(Exception exception) {
        return new QueryResponse(
            null,
            false,
            "Failed to get answer from AI service: " + exception.getMessage()
        );
    }

    /**
     * Create a network/timeout error response
     * @param serviceName The name of the service that failed
     * @return QueryResponse with connectivity error
     */
    public static QueryResponse createServiceUnavailableResponse(String serviceName) {
        return new QueryResponse(
            null,
            false,
            serviceName + " is currently unavailable. Please try again later."
        );
    }
}
