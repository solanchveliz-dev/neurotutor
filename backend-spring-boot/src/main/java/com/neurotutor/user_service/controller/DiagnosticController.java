package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.service.DiagnosticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DiagnosticController {

    @Autowired
    private DiagnosticService diagnosticService;

    // 🚀 EP-02 / HU-11: Recibir respuestas y asignar nivel
    @PostMapping("/diagnostic/submit")
    @PreAuthorize("#request.studentId == authentication.name")
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
    @GetMapping("/diagnostic/student/{id}")
    @PreAuthorize("#id.toString() == authentication.name")
    public ResponseEntity<?> getStudentProfile(@PathVariable("id") Long id) {
        try {
            StudentProfileResponse profile = diagnosticService.obtenerDatosDashboard(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            // 🚀 MEJORA: Ahora verás el error real en lugar de un 404 vacío
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }

    @GetMapping("/diagnostic/questions")
    public ResponseEntity<java.util.List<DiagnosticQuestionResponse>> getDiagnosticQuestions() {
        return ResponseEntity.ok(diagnosticService.getActiveQuestions());
    }

    @PostMapping("/diagnostic/submit-v2")
    @PreAuthorize("#request.studentId != null && #request.studentId.toString() == authentication.name")
    public ResponseEntity<?> submitDiagnosticV2(@RequestBody SubmitDiagnosticV2Request request) {
        try {
            DiagnosticResultResponse response = diagnosticService.submitDiagnosticV2(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/diagnostic/attempts/{attemptId}/review")
    public ResponseEntity<?> getDiagnosticReview(@PathVariable Long attemptId) {
        try {
            DiagnosticReviewResponse response = diagnosticService.getDiagnosticReview(attemptId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/students/{studentId}/diagnostic/latest-review")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<?> getLatestDiagnosticReview(@PathVariable Long studentId) {
        try {
            DiagnosticReviewResponse response = diagnosticService.getLatestDiagnosticReview(studentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    static class ErrorResponse {
        private String detail;

        public ErrorResponse(String detail) {
            this.detail = detail;
        }

        public String getDetail() {
            return detail;
        }
    }
}
