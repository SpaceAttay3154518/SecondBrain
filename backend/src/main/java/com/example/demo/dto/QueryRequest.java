package com.example.demo.dto;

public class QueryRequest {
    private String question;
    private String userId;

    public QueryRequest() {}

    public QueryRequest(String question, String userId) {
        this.question = question;
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
