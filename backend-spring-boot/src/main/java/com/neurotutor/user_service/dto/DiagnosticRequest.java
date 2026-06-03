package com.neurotutor.user_service.dto; // Revisa que coincida con tu paquete real

import java.util.List;

public class DiagnosticRequest {
    private String studentId;
    private List<String> respuestas;

    // Constructor vacío obligatorio para que Jackson (Spring) pueda procesar el JSON
    public DiagnosticRequest() {
    }

    public DiagnosticRequest(String studentId, List<String> respuestas) {
        this.studentId = studentId;
        this.respuestas = respuestas;
    }

    // Getters y Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }
}