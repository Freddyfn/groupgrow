package com.groupgrow.groupgrow.dto;

public class AiQuestionRequest {
    private String question;

    // Constructors
    public AiQuestionRequest() {
    }

    public AiQuestionRequest(String question) {
        this.question = question;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
