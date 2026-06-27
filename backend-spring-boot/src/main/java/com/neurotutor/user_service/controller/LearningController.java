package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
@CrossOrigin(origins = "*")
public class LearningController {

    @Autowired
    private LearningService learningService;

    @GetMapping("/content/{moduloId}")
    public ResponseEntity<LearningContentResponse> getContent(@PathVariable Long moduloId) {
        return ResponseEntity.ok(learningService.getLevelContent(moduloId));
    }

    @GetMapping("/modules/{levelId}/lessons")
    public ResponseEntity<List<TheoryLessonSummaryResponse>> getTheoryLessons(
            @PathVariable Long levelId) {
        return ResponseEntity.ok(learningService.getTheoryLessons(levelId));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<TheoryLessonDetailResponse> getTheoryLesson(
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(learningService.getTheoryLesson(lessonId));
    }

    @GetMapping("/exam/{moduloId}")
    public ResponseEntity<List<FinalExamQuestionResponse>> getExam(@PathVariable Long moduloId) {
        return ResponseEntity.ok(learningService.getFinalExam(moduloId));
    }

    @PostMapping("/exam-attempts")
    public ResponseEntity<SubmitFinalExamAttemptResponse> submitFinalExamAttempt(
            @RequestBody SubmitFinalExamAttemptRequest request) {
        return ResponseEntity.ok(learningService.submitFinalExamAttempt(request));
    }

    @PostMapping("/submit-exam")
    public ResponseEntity<String> submitExam(@RequestParam Long studentId,
                                             @RequestParam Long moduloId,
                                             @RequestParam int score) {
        String mensaje = learningService.procesarResultadoExamen(studentId, moduloId, score);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/topic-ruta/{moduloId}")
    public ResponseEntity<List<ModuleItem>> getTopicRuta(@PathVariable Long moduloId, @RequestParam Long studentId) {
        return ResponseEntity.ok(learningService.getTopicRuta(moduloId, studentId));
    }

    @PostMapping("/add-points")
    public ResponseEntity<Void> addPoints(@RequestParam Long studentId, @RequestParam int points) {
        learningService.sumarPuntos(studentId, points);
        return ResponseEntity.ok().build();
    }

    // ==================== NUEVOS ENDPOINTS HU-25 ====================

    @GetMapping("/exam-passed")
    public ResponseEntity<ExamPassedResponse> checkExamPassed(
            @RequestParam Long studentId,
            @RequestParam Long moduloId) {
        return ResponseEntity.ok(learningService.checkExamPassed(studentId, moduloId));
    }

    @PostMapping("/submit-exam-v2")
    public ResponseEntity<SubmitExamResponse> submitExamV2(@RequestBody SubmitExamRequest request) {
        return ResponseEntity.ok(learningService.procesarExamenV2(request));
    }
}
