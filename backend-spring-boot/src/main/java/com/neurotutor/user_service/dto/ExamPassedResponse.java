package com.neurotutor.user_service.dto;

public class ExamPassedResponse {
    private boolean alreadyPassed;
    private String passedAt;

    public ExamPassedResponse() {}

    public ExamPassedResponse(boolean alreadyPassed, String passedAt) {
        this.alreadyPassed = alreadyPassed;
        this.passedAt = passedAt;
    }

    public boolean isAlreadyPassed() {
        return alreadyPassed;
    }

    public void setAlreadyPassed(boolean alreadyPassed) {
        this.alreadyPassed = alreadyPassed;
    }

    public String getPassedAt() {
        return passedAt;
    }

    public void setPassedAt(String passedAt) {
        this.passedAt = passedAt;
    }
}