package com.neurotutor.user_service.dto;

public class AiTutorRequest {
    private Long studentId;
    private Long moduleId;
    private Long levelId;
    private Long lessonId;
    private Long exerciseId;
    private String currentScreen;
    private String action;
    private String message;
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

    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }
    public String getCurrentScreen() { return currentScreen; }
    public void setCurrentScreen(String currentScreen) { this.currentScreen = currentScreen; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

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
