package com.neurotutor.user_service.dto;

public class AiErrorResponse {
    private String error;

    public AiErrorResponse() {}

    public AiErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
