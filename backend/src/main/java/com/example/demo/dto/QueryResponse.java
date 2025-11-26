package com.example.demo.dto;

public class QueryResponse {
    private String answer;
    private boolean success;
    private String error;

    public QueryResponse() {}

    public QueryResponse(String answer, boolean success) {
        this.answer = answer;
        this.success = success;
    }

    public QueryResponse(String answer, boolean success, String error) {
        this.answer = answer;
        this.success = success;
        this.error = error;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
