package com.neurotutor.user_service.dto;

import java.util.List;

public class StudentProfileResponse {
    private String nombreCompleto;
    private String gradoSeccion;
    private String nivelActual;
    private int puntosTotales;
    private List<ModuleItem> modulos; // 🚀 CRÍTICO: Para HU-14

    public StudentProfileResponse(String nombreCompleto, String gradoSeccion,
                                  String nivelActual, int puntosTotales,
                                  List<ModuleItem> modulos) {
        this.nombreCompleto = nombreCompleto;
        this.gradoSeccion = gradoSeccion;
        this.nivelActual = nivelActual;
        this.puntosTotales = puntosTotales;
        this.modulos = modulos;
    }

    // Getters y Setters
    public String getNombreCompleto() { return nombreCompleto; }
    public String getGradoSeccion() { return gradoSeccion; }
    public String getNivelActual() { return nivelActual; }
    public int getPuntosTotales() { return puntosTotales; }
    public List<ModuleItem> getModulos() { return modulos; }
}