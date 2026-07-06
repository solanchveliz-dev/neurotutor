package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.CompleteTheoryRequest;
import com.neurotutor.user_service.dto.ModuleProgressResponse;
import com.neurotutor.user_service.dto.StudentProgressResponse;
import com.neurotutor.user_service.dto.SubmitPracticeAttemptRequest;
import com.neurotutor.user_service.dto.SubmitPracticeAttemptResponse;
import com.neurotutor.user_service.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/students/{studentId}/progress")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<StudentProgressResponse> getStudentProgress(@PathVariable Long studentId) {
        return ResponseEntity.ok(progressService.getStudentProgress(studentId));
    }

    @GetMapping("/students/{studentId}/modules/{moduloId}/progress")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<ModuleProgressResponse> getModuleProgress(@PathVariable Long studentId,
                                                                    @PathVariable Long moduloId) {
        return ResponseEntity.ok(progressService.getModuleProgress(studentId, moduloId));
    }

    @PostMapping("/students/{studentId}/modules/{moduloId}/theory/complete")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<ModuleProgressResponse> completeTheory(@PathVariable Long studentId,
                                                                 @PathVariable Long moduloId,
                                                                 @RequestBody(required = false)
                                                                 CompleteTheoryRequest request) {
        return ResponseEntity.ok(progressService.markTheoryCompleted(studentId, moduloId));
    }

    @PostMapping("/practice/attempts")
    @PreAuthorize("#request.studentId != null && #request.studentId.toString() == authentication.name")
    public ResponseEntity<SubmitPracticeAttemptResponse> submitPracticeAttempt(
            @RequestBody SubmitPracticeAttemptRequest request) {
        return ResponseEntity.ok(progressService.submitPracticeAttempt(request));
    }
}
