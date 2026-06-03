package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.StudentProfileResponse;
import com.neurotutor.user_service.service.DiagnosticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DiagnosticService diagnosticService; // 🚀 Usamos el service que me mostraste

    // 🚀 CAMBIO CLAVE: Cambiamos {email} por {id} y el tipo a Long
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentProfile(@PathVariable("id") Long id) {
        try {
            System.out.println("📡 Solicitud de Dashboard para Estudiante ID: " + id);

            // Llamamos a la lógica real de tu DiagnosticService
            StudentProfileResponse profile = diagnosticService.obtenerDatosDashboard(id);

            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            // Si el ID no existe en la base de datos
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Cualquier otro error técnico (ej. SQL)
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }
}