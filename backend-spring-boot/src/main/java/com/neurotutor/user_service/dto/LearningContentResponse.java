package com.neurotutor.user_service.dto;

import java.util.List;

public class LearningContentResponse {
    private String teoriaHtml;
    private List<Exercise> ejercicios;

    public LearningContentResponse(String teoriaHtml, List<Exercise> ejercicios) {
        this.teoriaHtml = teoriaHtml;
        this.ejercicios = ejercicios;
    }

    // Getters y Setters
    public String getTeoriaHtml() { return teoriaHtml; }
    public void setTeoriaHtml(String teoriaHtml) { this.teoriaHtml = teoriaHtml; }

    public List<Exercise> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Exercise> ejercicios) { this.ejercicios = ejercicios; }
}