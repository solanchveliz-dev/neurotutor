package com.neurotutor.user_service.dto;

public class AuthResponse {
    private String mensaje;
    private String email;
    private String token;

    public AuthResponse(String mensaje, String email, String token) {
        this.mensaje = mensaje;
        this.email = email;
        this.token = token;
    }

    // Getters
    public String getMensaje() { return mensaje; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
}