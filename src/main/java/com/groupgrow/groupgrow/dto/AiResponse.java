package com.groupgrow.groupgrow.dto;

public class AiResponse {
    private String answer;

    public AiResponse(String answer) {
        this.answer = answer;
    }

    // Getter
    public String getAnswer() {
        return answer;
    }

    // Setter
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
