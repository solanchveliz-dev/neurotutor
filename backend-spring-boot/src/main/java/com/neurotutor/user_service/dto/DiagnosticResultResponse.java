package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiagnosticResultResponse {
    private Long attemptId;
    private Long studentId;
    private int correctAnswers;
    private int totalQuestions;
    private int scorePercentage;
    private String assignedLevel;
    private String message;

    public DiagnosticResultResponse(Long attemptId, Long studentId, int correctAnswers,
                                    int totalQuestions, int scorePercentage,
                                    String assignedLevel, String message) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.scorePercentage = scorePercentage;
        this.assignedLevel = assignedLevel;
        this.message = message;
    }

    @JsonProperty("attempt_id")
    public Long getAttemptId() {
        return attemptId;
    }

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("correct_answers")
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    @JsonProperty("total_questions")
    public int getTotalQuestions() {
        return totalQuestions;
    }

    @JsonProperty("score_percentage")
    public int getScorePercentage() {
        return scorePercentage;
    }

    @JsonProperty("assigned_level")
    public String getAssignedLevel() {
        return assignedLevel;
    }

    public String getMessage() {
        return message;
    }
}
