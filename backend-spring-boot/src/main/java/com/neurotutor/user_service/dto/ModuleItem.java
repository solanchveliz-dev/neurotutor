package com.neurotutor.user_service.dto;

public class ModuleItem {
    private String id;
    private String titulo;
    private int ejerciciosCompletados;
    private int ejerciciosTotales;
    private String estado;
    private String temaNombre;
    private String nivelRequerido;// EN_CURSO, BLOQUEADO, COMPLETADO

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
}