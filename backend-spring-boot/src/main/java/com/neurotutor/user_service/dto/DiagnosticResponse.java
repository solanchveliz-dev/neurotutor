package com.neurotutor.user_service.dto; // Revisa que coincida con tu paquete real

public class DiagnosticResponse {
    private String nivel; // Aquí viajará "BASICO", "INTERMEDIO" o "AVANZADO"
    private String mensaje;

    // Constructor vacío obligatorio
    public DiagnosticResponse() {
    }

    public DiagnosticResponse(String nivel, String mensaje) {
        this.nivel = nivel;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}