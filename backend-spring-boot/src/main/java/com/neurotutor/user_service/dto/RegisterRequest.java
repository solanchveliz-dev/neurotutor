package com.neurotutor.user_service.dto;

public class RegisterRequest {
    private String email;
    private String nombreCompleto;
    private String grado;
    private String seccion;
    private String password;
    private String password2;

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPassword2() { return password2; }
    public void setPassword2(String password2) { this.password2 = password2; }
}