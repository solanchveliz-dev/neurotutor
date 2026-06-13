package com.neurotutor.user_service.dto;

public class SubmitExamRequest {
    private Long studentId;
    private Long moduloId;
    private String level;  // "B", "I", "A"
    private int score;

    public SubmitExamRequest() {}

    public SubmitExamRequest(Long studentId, Long moduloId, String level, int score) {
        this.studentId = studentId;
        this.moduloId = moduloId;
        this.level = level;
        this.score = score;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getModuloId() {
        return moduloId;
    }

    public void setModuloId(Long moduloId) {
        this.moduloId = moduloId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}