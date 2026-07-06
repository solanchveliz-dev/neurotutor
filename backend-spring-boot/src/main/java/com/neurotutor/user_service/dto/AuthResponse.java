package com.neurotutor.user_service.dto;

public class AuthResponse {
    private String mensaje;
    private String email;
    private String token;
    private String id;               // 🚀 NUEVO: Para que Android sepa quién es el usuario
    private boolean examenCompletado; // 🚀 NUEVO: Para saber si mandarlo al Test o al Dashboard

    public AuthResponse(String mensaje, String email, String token, String id, boolean examenCompletado) {
        this.mensaje = mensaje;
        this.email = email;
        this.token = token;
        this.id = id;
        this.examenCompletado = examenCompletado;
    }

    // Getters y Setters
    public String getMensaje() { return mensaje; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
    public String getId() { return id; }
    public boolean isExamenCompletado() { return examenCompletado; }
}