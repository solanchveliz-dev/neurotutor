package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.DiagnosticRequest;
import com.neurotutor.user_service.dto.DiagnosticResponse;
import com.neurotutor.user_service.dto.StudentProfileResponse;
import com.neurotutor.user_service.service.DiagnosticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    @Autowired
    private DiagnosticService diagnosticService;

    // 🚀 EP-02 / HU-11: Recibir respuestas y asignar nivel
    @PostMapping("/submit")
    public ResponseEntity<DiagnosticResponse> submitDiagnostic(@RequestBody DiagnosticRequest request) {
        try {
            DiagnosticResponse response = diagnosticService.calcularYGuardarNivel(request);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new DiagnosticResponse("ERROR", "ID de estudiante inválido."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new DiagnosticResponse("ERROR", e.getMessage()));
        }
    }

    // 🚀 EP-02 / HU-14: Obtener datos reales para el Dashboard de Android
    // Este es el endpoint que tu App de Android está intentando llamar
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentProfile(@PathVariable("id") Long id) {
        try {
            StudentProfileResponse profile = diagnosticService.obtenerDatosDashboard(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            // 🚀 MEJORA: Ahora verás el error real en lugar de un 404 vacío
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }
}