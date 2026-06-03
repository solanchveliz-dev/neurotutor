package com.neurotutor.user_service.dto;

public class ModuleItem {
    private String id;
    private String titulo;
    private int ejerciciosCompletados;
    private int ejerciciosTotales;
    private String estado; // EN_CURSO, BLOQUEADO, COMPLETADO

    public ModuleItem(String id, String titulo, int ejerciciosCompletados,
                      int ejerciciosTotales, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.ejerciciosCompletados = ejerciciosCompletados;
        this.ejerciciosTotales = ejerciciosTotales;
        this.estado = estado;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getEjerciciosCompletados() { return ejerciciosCompletados; }
    public int getEjerciciosTotales() { return ejerciciosTotales; }
    public String getEstado() { return estado; }
}