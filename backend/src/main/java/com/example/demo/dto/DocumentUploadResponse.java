package com.example.demo.dto;

public class DocumentUploadResponse {
    private boolean success;
    private String message;
    private String documentId;

    public DocumentUploadResponse() {}

    public DocumentUploadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public DocumentUploadResponse(boolean success, String message, String documentId) {
        this.success = success;
        this.message = message;
        this.documentId = documentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
