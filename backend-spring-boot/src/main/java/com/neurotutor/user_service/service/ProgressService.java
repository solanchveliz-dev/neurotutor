package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.ModuleProgressResponse;
import com.neurotutor.user_service.dto.StudentProgressResponse;
import com.neurotutor.user_service.dto.SubmitPracticeAttemptRequest;
import com.neurotutor.user_service.dto.SubmitPracticeAttemptResponse;
import com.neurotutor.user_service.model.*;
import com.neurotutor.user_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressService {

    private static final int THEORY_PROGRESS = 33;
    private static final int PRACTICE_PROGRESS = 33;
    private static final int EXAM_PROGRESS = 34;
    private static final int PASSING_SCORE = 70;

    private final StudentModuleProgressRepository progressRepository;
    private final PracticeAttemptRepository practiceAttemptRepository;
    private final PracticeAnswerRepository practiceAnswerRepository;
    private final EstudianteRepository estudianteRepository;
    private final ModuloRepository moduloRepository;
    private final EjercicioRepository ejercicioRepository;
    private final AchievementService achievementService;

    public ProgressService(StudentModuleProgressRepository progressRepository,
                           PracticeAttemptRepository practiceAttemptRepository,
                           PracticeAnswerRepository practiceAnswerRepository,
                           EstudianteRepository estudianteRepository,
                           ModuloRepository moduloRepository,
                           EjercicioRepository ejercicioRepository,
                           AchievementService achievementService) {
        this.progressRepository = progressRepository;
        this.practiceAttemptRepository = practiceAttemptRepository;
        this.practiceAnswerRepository = practiceAnswerRepository;
        this.estudianteRepository = estudianteRepository;
        this.moduloRepository = moduloRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.achievementService = achievementService;
    }

    @Transactional(readOnly = true)
    public StudentProgressResponse getStudentProgress(Long studentId) {
        Estudiante student = getStudent(studentId);
        List<ModuleProgressResponse> modules = progressRepository.findByStudentId(studentId).stream()
                .map(this::toModuleProgressResponse)
                .toList();

        int overallProgress = modules.isEmpty()
                ? 0
                : Math.round((float) modules.stream()
                .mapToInt(ModuleProgressResponse::getProgressPercentage)
                .sum() / modules.size());

        return new StudentProgressResponse(
                student.getId(),
                overallProgress,
                student.getPuntosTotales(),
                0,
                modules
        );
    }

    @Transactional
    public ModuleProgressResponse getModuleProgress(Long studentId, Long moduloId) {
        StudentModuleProgress progress = getOrCreateProgress(studentId, moduloId);
        return toModuleProgressResponse(progress);
    }

    @Transactional
    public ModuleProgressResponse markTheoryCompleted(Long studentId, Long moduloId) {
        StudentModuleProgress progress = getOrCreateProgress(studentId, moduloId);
        progress.setTheoryCompleted(true);
        touchAndRecalculate(progress);
        StudentModuleProgress savedProgress = progressRepository.save(progress);
        achievementService.evaluateStudentAchievements(studentId);
        return toModuleProgressResponse(savedProgress);
    }

    @Transactional
    public SubmitPracticeAttemptResponse submitPracticeAttempt(SubmitPracticeAttemptRequest request) {
        validatePracticeRequest(request);

        Estudiante student = getStudent(request.getStudentId());
        Modulo modulo = getModulo(request.getModuloId());
        StudentModuleProgress progress = getOrCreateProgress(student.getId(), modulo.getId());
        boolean wasPracticeCompleted = progress.isPracticeCompleted();

        LocalDateTime now = LocalDateTime.now();
        List<PracticeAnswer> practiceAnswers = new ArrayList<>();
        int correctAnswers = 0;
        int rawPointsEarned = 0;

        PracticeAttempt attempt = new PracticeAttempt();
        attempt.setStudent(student);
        attempt.setModulo(modulo);
        attempt.setStartedAt(now);
        attempt.setCompletedAt(now);

        PracticeAttempt savedAttempt = practiceAttemptRepository.save(attempt);

        for (SubmitPracticeAttemptRequest.PracticeAnswerRequest answerRequest : request.getAnswers()) {
            Ejercicio exercise = ejercicioRepository.findById(answerRequest.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

            if (exercise.getModulo() == null || !exercise.getModulo().getId().equals(modulo.getId())) {
                throw new RuntimeException("El ejercicio no pertenece al modulo indicado");
            }

            boolean correct = exercise.getRespuestaCorrectaIndex() == answerRequest.getSelectedAnswerIndex();
            if (correct) {
                correctAnswers++;
                rawPointsEarned += exercise.getPuntos();
            }

            PracticeAnswer practiceAnswer = new PracticeAnswer();
            practiceAnswer.setAttempt(savedAttempt);
            practiceAnswer.setExercise(exercise);
            practiceAnswer.setSelectedAnswerIndex(answerRequest.getSelectedAnswerIndex());
            practiceAnswer.setCorrect(correct);
            practiceAnswers.add(practiceAnswer);
        }

        int totalQuestions = request.getAnswers().size();
        int scorePercentage = Math.round((correctAnswers * 100f) / totalQuestions);
        boolean practiceCompleted = scorePercentage >= PASSING_SCORE;
        int pointsEarned = practiceCompleted && !wasPracticeCompleted ? rawPointsEarned : 0;

        savedAttempt.setTotalQuestions(totalQuestions);
        savedAttempt.setCorrectAnswers(correctAnswers);
        savedAttempt.setScorePercentage(scorePercentage);
        savedAttempt.setPointsEarned(pointsEarned);
        practiceAttemptRepository.save(savedAttempt);
        practiceAnswerRepository.saveAll(practiceAnswers);

        if (pointsEarned > 0) {
            student.setPuntosTotales(student.getPuntosTotales() + pointsEarned);
            estudianteRepository.save(student);
        }

        progress.setPracticeCompleted(progress.isPracticeCompleted() || practiceCompleted);
        progress.setPracticeCompletedCount(Math.max(progress.getPracticeCompletedCount(), correctAnswers));
        progress.setPracticeTotalCount(Math.max(progress.getPracticeTotalCount(), totalQuestions));
        touchAndRecalculate(progress);
        StudentModuleProgress savedProgress = progressRepository.save(progress);
        achievementService.evaluateStudentAchievements(student.getId());

        return new SubmitPracticeAttemptResponse(
                savedAttempt.getId(),
                correctAnswers,
                totalQuestions,
                scorePercentage,
                pointsEarned,
                savedProgress.isPracticeCompleted(),
                savedProgress.getProgressPercentage()
        );
    }

    @Transactional
    public void updateExamProgress(Long studentId, Long moduloId, int score, boolean passed, int pointsEarned) {
        StudentModuleProgress progress = getOrCreateProgress(studentId, moduloId);
        progress.setExamBestScore(Math.max(progress.getExamBestScore(), score));
        progress.setExamPassed(progress.isExamPassed() || passed);
        touchAndRecalculate(progress);
        progressRepository.save(progress);
        achievementService.evaluateStudentAchievements(studentId);
    }

    private void validatePracticeRequest(SubmitPracticeAttemptRequest request) {
        if (request == null) {
            throw new RuntimeException("La solicitud de practica es obligatoria");
        }
        if (request.getStudentId() == null) {
            throw new RuntimeException("student_id es obligatorio");
        }
        if (request.getModuloId() == null) {
            throw new RuntimeException("modulo_id es obligatorio");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("answers es obligatorio");
        }
    }

    private StudentModuleProgress getOrCreateProgress(Long studentId, Long moduloId) {
        return progressRepository.findByStudentIdAndModuloId(studentId, moduloId)
                .orElseGet(() -> {
                    StudentModuleProgress progress = new StudentModuleProgress();
                    progress.setStudent(getStudent(studentId));
                    progress.setModulo(getModulo(moduloId));
                    progress.setProgressPercentage(0);
                    return progressRepository.save(progress);
                });
    }

    private Estudiante getStudent(Long studentId) {
        return estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
    }

    private Modulo getModulo(Long moduloId) {
        return moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RuntimeException("Modulo no encontrado"));
    }

    private void touchAndRecalculate(StudentModuleProgress progress) {
        progress.setLastActivityAt(LocalDateTime.now());
        progress.setProgressPercentage(calculateProgress(progress));

        if (progress.isTheoryCompleted()
                && progress.isPracticeCompleted()
                && progress.isExamPassed()
                && progress.getCompletedAt() == null) {
            progress.setCompletedAt(progress.getLastActivityAt());
        }
    }

    private int calculateProgress(StudentModuleProgress progress) {
        int percentage = 0;
        if (progress.isTheoryCompleted()) {
            percentage += THEORY_PROGRESS;
        }
        if (progress.isPracticeCompleted()) {
            percentage += PRACTICE_PROGRESS;
        }
        if (progress.isExamPassed()) {
            percentage += EXAM_PROGRESS;
        }
        return percentage;
    }

    private ModuleProgressResponse toModuleProgressResponse(StudentModuleProgress progress) {
        Modulo modulo = progress.getModulo();
        return new ModuleProgressResponse(
                modulo.getId(),
                modulo.getTitulo(),
                modulo.getNivelRequerido(),
                progress.isTheoryCompleted(),
                progress.isPracticeCompleted(),
                progress.isExamPassed(),
                progress.getPracticeCompletedCount(),
                progress.getPracticeTotalCount(),
                progress.getExamBestScore(),
                progress.getProgressPercentage(),
                progress.getLastActivityAt(),
                progress.getCompletedAt()
        );
    }
}
