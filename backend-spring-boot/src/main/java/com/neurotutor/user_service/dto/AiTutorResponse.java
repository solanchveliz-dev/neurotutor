package com.neurotutor.user_service.dto;

public class AiTutorResponse {
    private String answer;

    public AiTutorResponse() {}

    public AiTutorResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
