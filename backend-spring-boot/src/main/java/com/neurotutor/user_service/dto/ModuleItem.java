package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModuleItem {
    private String id;
    private String titulo;
    private int ejerciciosCompletados;
    private int ejerciciosTotales;
    private String estado;
    private String temaNombre;
    private String nivelRequerido;// EN_CURSO, BLOQUEADO, COMPLETADO
    private int progressPercentage;
    private boolean theoryCompleted;
    private boolean practiceCompleted;
    private boolean examPassed;

    public ModuleItem(String id, String titulo, int ejerciciosCompletados,
                      int ejerciciosTotales, String estado , String temaNombre, String nivelRequerido) {
        this.id = id;
        this.titulo = titulo;
        this.ejerciciosCompletados = ejerciciosCompletados;
        this.ejerciciosTotales = ejerciciosTotales;
        this.estado = estado;
        this.temaNombre=temaNombre;
        this.nivelRequerido = nivelRequerido;
    }

    public ModuleItem(String id, String titulo, int ejerciciosCompletados,
                      int ejerciciosTotales, String estado, String temaNombre,
                      String nivelRequerido, int progressPercentage,
                      boolean theoryCompleted, boolean practiceCompleted,
                      boolean examPassed) {
        this(id, titulo, ejerciciosCompletados, ejerciciosTotales, estado, temaNombre, nivelRequerido);
        this.progressPercentage = progressPercentage;
        this.theoryCompleted = theoryCompleted;
        this.practiceCompleted = practiceCompleted;
        this.examPassed = examPassed;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getEjerciciosCompletados() { return ejerciciosCompletados; }
    public int getEjerciciosTotales() { return ejerciciosTotales; }
    public String getEstado() { return estado; }

    public String getTemaNombre() {
        return temaNombre;
    }

    public void setTemaNombre(String temaNombre) {
        this.temaNombre = temaNombre;
    }
    public String getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(String nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

    @JsonProperty("progress_percentage")
    public int getProgressPercentage() { return progressPercentage; }

    @JsonProperty("theory_completed")
    public boolean isTheoryCompleted() { return theoryCompleted; }

    @JsonProperty("practice_completed")
    public boolean isPracticeCompleted() { return practiceCompleted; }

    @JsonProperty("exam_passed")
    public boolean isExamPassed() { return examPassed; }
}
