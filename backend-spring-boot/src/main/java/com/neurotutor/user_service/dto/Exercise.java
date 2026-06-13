package com.neurotutor.user_service.dto;

import java.util.List;

public class Exercise {
    private String id;
    private String question;
    private List<String> options;
    private int correctAnswerIndex;
    private String tutorExplanation;
    private int points;

    public Exercise(String id, String question, List<String> options,
                       int correctAnswerIndex, String tutorExplanation, int points) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.tutorExplanation = tutorExplanation;
        this.points = points;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }

    public String getTutorExplanation() { return tutorExplanation; }
    public void setTutorExplanation(String tutorExplanation) { this.tutorExplanation = tutorExplanation; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}