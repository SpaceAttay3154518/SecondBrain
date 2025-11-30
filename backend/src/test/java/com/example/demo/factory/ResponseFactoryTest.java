package com.example.demo.factory;

import com.example.demo.dto.QueryResponse;
import com.example.demo.dto.DocumentUploadResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResponseFactory to ensure correct response creation
 */
class ResponseFactoryTest {

    @Test
    void testCreateSuccessQueryResponse() {
        String answer = "This is the AI's answer";
        QueryResponse response = ResponseFactory.createSuccessQueryResponse(answer);
        
        assertEquals(answer, response.getAnswer());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    @Test
    void testCreateErrorQueryResponse() {
        String errorMessage = "AI service is unavailable";
        QueryResponse response = ResponseFactory.createErrorQueryResponse(errorMessage);
        
        assertNull(response.getAnswer());
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void testCreatePartialQueryResponse() {
        String partialAnswer = "Partial response";
        String errorMessage = "Connection timeout";
        QueryResponse response = ResponseFactory.createPartialQueryResponse(partialAnswer, errorMessage);
        
        assertEquals(partialAnswer, response.getAnswer());
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void testCreateSuccessUploadResponseWithId() {
        String documentId = "user123";
        DocumentUploadResponse response = ResponseFactory.createSuccessUploadResponse(documentId);
        
        assertTrue(response.isSuccess());
        assertEquals("Document uploaded successfully", response.getMessage());
        assertEquals(documentId, response.getDocumentId());
    }

    @Test
    void testCreateSuccessUploadResponseWithoutId() {
        DocumentUploadResponse response = ResponseFactory.createSuccessUploadResponse();
        
        assertTrue(response.isSuccess());
        assertEquals("Document uploaded successfully", response.getMessage());
        assertNull(response.getDocumentId());
    }

    @Test
    void testCreateUnsupportedFileTypeResponse() {
        DocumentUploadResponse response = ResponseFactory.createUnsupportedFileTypeResponse();
        
        assertFalse(response.isSuccess());
        assertEquals("Only PDF, TXT, and MD files are supported", response.getMessage());
    }

    @Test
    void testCreateUploadExceptionResponse() {
        Exception exception = new RuntimeException("Network error");
        DocumentUploadResponse response = ResponseFactory.createUploadExceptionResponse(exception);
        
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to upload document"));
        assertTrue(response.getMessage().contains("Network error"));
    }

    @Test
    void testCreateAIServiceErrorResponse() {
        Exception exception = new RuntimeException("Connection refused");
        QueryResponse response = ResponseFactory.createAIServiceErrorResponse(exception);
        
        assertNull(response.getAnswer());
        assertFalse(response.isSuccess());
        assertTrue(response.getError().contains("Failed to get answer from AI service"));
        assertTrue(response.getError().contains("Connection refused"));
    }

    @Test
    void testCreateServiceUnavailableResponse() {
        String serviceName = "AI Model";
        QueryResponse response = ResponseFactory.createServiceUnavailableResponse(serviceName);
        
        assertNull(response.getAnswer());
        assertFalse(response.isSuccess());
        assertTrue(response.getError().contains("AI Model"));
        assertTrue(response.getError().contains("currently unavailable"));
    }
}
