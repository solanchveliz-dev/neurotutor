package com.neurotutor.user_service.dto;

public class AiTutorRequest {
    private Long studentId;
    private Long moduleId;
    private String question;
    private String context;

    public AiTutorRequest() {}

    public AiTutorRequest(Long studentId, Long moduleId, String question, String context) {
        this.studentId = studentId;
        this.moduleId = moduleId;
        this.question = question;
        this.context = context;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
