package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AiTutorResponse {
    private String answer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String structuredContent;

    public AiTutorResponse() {}

    public AiTutorResponse(String answer) {
        this.answer = answer;
    }

    public AiTutorResponse(String answer, String structuredContent) {
        this.answer = answer;
        this.structuredContent = structuredContent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStructuredContent() {
        return structuredContent;
    }

    public void setStructuredContent(String structuredContent) {
        this.structuredContent = structuredContent;
    }
}
