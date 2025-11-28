package com.example.demo.dto;

public class QueryRequest {
    private String question;
    private String id;  // AI Model expects "id" field

    public QueryRequest() {}

    public QueryRequest(String question, String id) {
        this.question = question;
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
