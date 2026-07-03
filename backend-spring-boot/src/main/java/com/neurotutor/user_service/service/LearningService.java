package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.*;
import com.neurotutor.user_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LearningService {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private FinalExamAttemptRepository finalExamAttemptRepository;

    @Autowired
    private FinalExamAnswerRepository finalExamAnswerRepository;

    @Autowired
    private TheoryLessonRepository theoryLessonRepository;

    @Autowired
    private StudentModuleProgressRepository studentModuleProgressRepository;

    @Transactional(readOnly = true)
    public LearningModuleDetailsResponse getModuleDetails(Long moduleId) {
        Modulo requestedModule = findModule(moduleId);
        List<Modulo> levels = getOrderedTopicLevels(requestedModule);
        Long canonicalModuleId = levels.get(0).getId();
        Tema topic = requestedModule.getTema();
        String title = topic != null && topic.getNombre() != null
                ? topic.getNombre()
                : requestedModule.getTitulo();
        String description = topic == null ? null : topic.getDescripcion();

        return new LearningModuleDetailsResponse(
                moduleId,
                title,
                description,
                levels.stream()
                        .map(level -> toLevelDetails(level, canonicalModuleId))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public LearningLevelDetailsResponse getLevelDetails(Long levelId) {
        Modulo level = findModule(levelId);
        List<Modulo> levels = getOrderedTopicLevels(level);
        return toLevelDetails(level, levels.get(0).getId());
    }

    private Modulo findModule(Long moduleId) {
        return moduloRepository.findById(moduleId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Modulo no encontrado"));
    }

    private List<Modulo> getOrderedTopicLevels(Modulo module) {
        List<Modulo> levels = module.getTema() == null
                ? List.of(module)
                : moduloRepository.findByTemaId(module.getTema().getId());
        if (levels.isEmpty()) {
            levels = List.of(module);
        }
        return levels.stream()
                .sorted(Comparator.comparingInt(this::levelOrder).thenComparing(Modulo::getId))
                .toList();
    }

    private int levelOrder(Modulo module) {
        return switch (module.getNivelRequerido() == null ? "" : module.getNivelRequerido()) {
            case "BASICO" -> 0;
            case "INTERMEDIO" -> 1;
            case "AVANZADO" -> 2;
            default -> 3;
        };
    }

    private LearningLevelDetailsResponse toLevelDetails(Modulo level, Long canonicalModuleId) {
        List<TheoryLesson> lessons = theoryLessonRepository
                .findByModuloIdAndActiveTrueOrderByOrderNumberAsc(level.getId());
        String description = lessons.isEmpty() ? null : lessons.get(0).getSummary();

        return new LearningLevelDetailsResponse(
                level.getId(),
                canonicalModuleId,
                level.getTitulo(),
                level.getNivelRequerido(),
                description,
                theoryLessonRepository.countByModuloIdAndActiveTrue(level.getId()),
                ejercicioRepository.countByModuloIdAndEsExamenFinal(level.getId(), false),
                ejercicioRepository.countByModuloIdAndEsExamenFinal(level.getId(), true)
        );
    }

    /**
     * 🚀 HU-20: Obtiene la ruta completa de niveles (🌱, 🔥, 🚀) para un tema.
     * Implementa las reglas de bloqueo según el nivel del estudiante.
     */
    public List<ModuleItem> getTopicRuta(Long moduloId, Long studentId) {
        Modulo moduloRef = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        Tema tema = moduloRef.getTema();
        Estudiante estudiante = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        List<ModuleItem> ruta = new ArrayList<>();
        for (Modulo modulo : tema.getNiveles()) {
            StudentModuleProgress progress = studentModuleProgressRepository
                    .findByStudentIdAndModuloId(studentId, modulo.getId())
                    .orElse(null);
            boolean theoryCompleted = progress != null && progress.isTheoryCompleted();
            boolean practiceCompleted = progress != null && progress.isPracticeCompleted();
            boolean examPassed = progress != null && progress.isExamPassed();
            boolean completed = theoryCompleted && practiceCompleted && examPassed;
            boolean unlocked = levelRank(estudiante.getNivelDiagnostico())
                    >= levelRank(modulo.getNivelRequerido());
            int progressPercentage = calculateAcademicProgress(
                    theoryCompleted, practiceCompleted, examPassed);
            String estado = !unlocked
                    ? "BLOQUEADO"
                    : completed ? "COMPLETADO" : "EN_CURSO";

            ruta.add(new ModuleItem(
                    modulo.getId().toString(),
                    modulo.getTitulo(),
                    progress == null ? 0 : progress.getPracticeCompletedCount(),
                    modulo.getEjerciciosTotales(),
                    estado,
                    modulo.getTema().getNombre(),
                    modulo.getNivelRequerido(),
                    progressPercentage,
                    theoryCompleted,
                    practiceCompleted,
                    examPassed
            ));
        }
        return ruta;
    }

    private int levelRank(String level) {
        return switch (level == null ? "" : level) {
            case "BASICO", "B" -> 0;
            case "INTERMEDIO", "I" -> 1;
            case "AVANZADO", "A" -> 2;
            default -> -1;
        };
    }

    private int calculateAcademicProgress(boolean theoryCompleted,
                                          boolean practiceCompleted,
                                          boolean examPassed) {
        int percentage = 0;
        if (theoryCompleted) percentage += 33;
        if (practiceCompleted) percentage += 33;
        if (examPassed) percentage += 34;
        return percentage;
    }

    /**
     * 🚀 HU-22: Suma puntos al estudiante tras completar una práctica.
     */
    @Transactional
    public void sumarPuntos(Long studentId, int puntos) {
        Estudiante e = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        e.setPuntosTotales(e.getPuntosTotales() + puntos);
        estudianteRepository.save(e);
    }

    // 📖 HU-21/22: Cargar teoría y ejercicios
    public LearningContentResponse getLevelContent(Long moduloId) {
        Modulo m = moduloRepository.findById(moduloId).get();
        List<Exercise> ejercicios = ejercicioRepository.findByModuloIdAndEsExamenFinal(moduloId, false)
                .stream().map(e -> new Exercise(e.getId().toString(), e.getEnunciado(), e.getOpciones(),
                        e.getRespuestaCorrectaIndex(), e.getExplicacionTutorIa(), e.getPuntos(), e.getSubtema()))
                .collect(Collectors.toList());
        return new LearningContentResponse(m.getTeoriaHtml(), ejercicios);
    }

    // 📝 HU-23: Cargar Examen
    public List<FinalExamQuestionResponse> getFinalExam(Long moduloId) {
        return ejercicioRepository.findByModuloIdAndEsExamenFinal(moduloId, true)
                .stream().map(e -> new FinalExamQuestionResponse(
                        e.getId(), e.getEnunciado(), e.getImagenUrl(), e.getOpciones()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TheoryLessonSummaryResponse> getTheoryLessons(Long moduloId) {
        if (!moduloRepository.existsById(moduloId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Modulo no encontrado");
        }
        return theoryLessonRepository.findByModuloIdAndActiveTrueOrderByOrderNumberAsc(moduloId)
                .stream()
                .map(lesson -> new TheoryLessonSummaryResponse(
                        lesson.getId(), lesson.getTitle(), lesson.getSubtitle(), lesson.getSummary(),
                        lesson.getIcon(), lesson.getOrderNumber()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TheoryLessonDetailResponse getTheoryLesson(Long lessonId) {
        TheoryLesson lesson = theoryLessonRepository.findByIdAndActiveTrue(lessonId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Leccion no encontrada"));
        return new TheoryLessonDetailResponse(
                lesson.getId(), lesson.getModulo().getId(), lesson.getTitle(), lesson.getSubtitle(),
                lesson.getSummary(), lesson.getIcon(), lesson.getContentHtml(), lesson.getOrderNumber());
    }

    @Transactional
    public SubmitFinalExamAttemptResponse submitFinalExamAttempt(SubmitFinalExamAttemptRequest request) {
        validateFinalExamAttempt(request);

        Estudiante student = estudianteRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Modulo modulo = moduloRepository.findById(request.getModuloId())
                .orElseThrow(() -> new RuntimeException("Modulo no encontrado"));

        List<Ejercicio> examQuestions = ejercicioRepository
                .findByModuloIdAndEsExamenFinal(modulo.getId(), true);
        if (examQuestions.isEmpty()) {
            throw new RuntimeException("El modulo no tiene preguntas de examen final");
        }

        Map<Long, Ejercicio> questionsById = examQuestions.stream()
                .collect(Collectors.toMap(Ejercicio::getId, question -> question));
        Set<Long> submittedQuestionIds = new HashSet<>();
        List<FinalExamAnswer> answersToSave = new ArrayList<>();
        int correctAnswers = 0;

        for (SubmitFinalExamAttemptRequest.AnswerRequest answerRequest : request.getAnswers()) {
            if (answerRequest.getQuestionId() == null || answerRequest.getSelectedAnswerIndex() == null) {
                throw new RuntimeException("Cada respuesta debe incluir question_id y selected_answer_index");
            }
            if (!submittedQuestionIds.add(answerRequest.getQuestionId())) {
                throw new RuntimeException("No se permiten preguntas duplicadas");
            }

            Ejercicio question = questionsById.get(answerRequest.getQuestionId());
            if (question == null) {
                throw new RuntimeException("La pregunta no pertenece al examen indicado");
            }
            int selectedIndex = answerRequest.getSelectedAnswerIndex();
            if (selectedIndex < 0 || selectedIndex >= question.getOpciones().size()) {
                throw new RuntimeException("selected_answer_index no es valido");
            }

            boolean correct = question.getRespuestaCorrectaIndex() == selectedIndex;
            if (correct) {
                correctAnswers++;
            }

            FinalExamAnswer answer = new FinalExamAnswer();
            answer.setExercise(question);
            answer.setSelectedAnswerIndex(selectedIndex);
            answer.setCorrect(correct);
            answersToSave.add(answer);
        }

        if (submittedQuestionIds.size() != examQuestions.size()
                || !submittedQuestionIds.equals(questionsById.keySet())) {
            throw new RuntimeException("Debes responder todas las preguntas del examen");
        }

        int totalQuestions = examQuestions.size();
        int scorePercentage = Math.round((correctAnswers * 100f) / totalQuestions);
        boolean passed = scorePercentage >= 70;
        boolean alreadyPassed = progressService
                .getModuleProgress(student.getId(), modulo.getId())
                .isExamPassed();
        int pointsEarned = 0;

        if (passed && !alreadyPassed) {
            pointsEarned = 100;
            student.setPuntosTotales(student.getPuntosTotales() + pointsEarned);
            estudianteRepository.save(student);
        }

        FinalExamAttempt attempt = new FinalExamAttempt();
        attempt.setStudent(student);
        attempt.setModulo(modulo);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setScorePercentage(scorePercentage);
        attempt.setPassed(passed);
        attempt.setPointsEarned(pointsEarned);
        attempt.setCompletedAt(java.time.LocalDateTime.now());
        FinalExamAttempt savedAttempt = finalExamAttemptRepository.save(attempt);

        answersToSave.forEach(answer -> answer.setAttempt(savedAttempt));
        finalExamAnswerRepository.saveAll(answersToSave);

        List<String> unlockedAchievementCodes = progressService.updateExamProgress(
                student.getId(), modulo.getId(), scorePercentage, passed, pointsEarned);
        int moduleProgress = progressService
                .getModuleProgress(student.getId(), modulo.getId())
                .getProgressPercentage();
        boolean alreadyCompleted = student.getModulosCompletados().stream()
                .anyMatch(completedModule -> completedModule.getId().equals(modulo.getId()));
        if (moduleProgress >= 100 && !alreadyCompleted) {
            student.getModulosCompletados().add(modulo);
            promoteStudentLevel(student, modulo);
            estudianteRepository.save(student);
        }

        String message = passed
                ? (alreadyPassed ? "Ya habias aprobado este examen." : "¡Aprobaste el examen!")
                : "Necesitas 70% para aprobar.";

        String unlockedBadgeId = passed && !alreadyPassed
                ? examBadgeId(modulo.getNivelRequerido())
                : null;
        return new SubmitFinalExamAttemptResponse(
                savedAttempt.getId(), correctAnswers, totalQuestions, scorePercentage,
                passed, pointsEarned, message, moduleProgress, unlockedBadgeId,
                unlockedAchievementCodes);
    }

    private void validateFinalExamAttempt(SubmitFinalExamAttemptRequest request) {
        if (request == null || request.getStudentId() == null || request.getModuloId() == null) {
            throw new RuntimeException("student_id y modulo_id son obligatorios");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("answers es obligatorio");
        }
    }

    private void promoteStudentLevel(Estudiante student, Modulo modulo) {
        if ("BASICO".equals(modulo.getNivelRequerido())
                && "BASICO".equals(student.getNivelDiagnostico())) {
            student.setNivelDiagnostico("INTERMEDIO");
        } else if ("INTERMEDIO".equals(modulo.getNivelRequerido())
                && "INTERMEDIO".equals(student.getNivelDiagnostico())) {
            student.setNivelDiagnostico("AVANZADO");
        }
    }

    // 🚀 HU-24: Promoción de nivel automática
    private String examBadgeId(String level) {
        return switch (level) {
            case "BASICO" -> "basic_3";
            case "INTERMEDIO" -> "intermediate_3";
            case "AVANZADO" -> "advanced_3";
            default -> null;
        };
    }

    @Transactional
    public String procesarResultadoExamen(Long studentId, Long moduloId, int score) {
        if (score < 70) return "Necesitas 70% para aprobar.";
        Estudiante e = estudianteRepository.findById(studentId).get();
        Modulo m = moduloRepository.findById(moduloId).get();

        progressService.updateExamProgress(studentId, moduloId, score, true, 0);
        ModuleProgressResponse progress = progressService.getModuleProgress(studentId, moduloId);
        if (progress.getProgressPercentage() < 100) {
            return "Examen aprobado. Completa teoría y práctica para finalizar el módulo.";
        }

        boolean alreadyCompleted = e.getModulosCompletados().stream()
                .anyMatch(modulo -> modulo.getId().equals(moduloId));
        if (!alreadyCompleted) {
            e.getModulosCompletados().add(m);
            if ("BASICO".equals(m.getNivelRequerido()) && "BASICO".equals(e.getNivelDiagnostico())) {
                e.setNivelDiagnostico("INTERMEDIO");
            } else if ("INTERMEDIO".equals(m.getNivelRequerido())
                    && "INTERMEDIO".equals(e.getNivelDiagnostico())) {
                e.setNivelDiagnostico("AVANZADO");
            }
            estudianteRepository.save(e);
        }
        return "¡Nivel superado! Has desbloqueado el siguiente desafío.";
    }

    /**
     * 🔍 Verifica si el estudiante ya aprobó este examen antes
     */
    public ExamPassedResponse checkExamPassed(Long studentId, Long moduloId) {
        Estudiante estudiante = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Verificar si el estudiante ya tiene este módulo como completado
        boolean alreadyPassed = estudiante.getModulosCompletados().stream()
                .anyMatch(m -> m.getId().equals(moduloId));

        return new ExamPassedResponse(alreadyPassed, null);
    }

    /**
     * 🎯 HU-25: Procesa examen con lógica de puntos (solo primera vez) y desbloqueo
     */
    @Transactional
    public SubmitExamResponse procesarExamenV2(SubmitExamRequest request) {
        Long studentId = request.getStudentId();
        Long moduloId = request.getModuloId();
        int score = request.getScore();

        Estudiante estudiante = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Modulo modulo = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        boolean passed = score >= 70;

        if (!passed) {
            progressService.updateExamProgress(studentId, moduloId, score, false, 0);
            return new SubmitExamResponse(false, "No alcanzaste el 70%. ¡Sigue practicando!", 0, false, null, false);
        }

        ModuleProgressResponse progressBefore =
                progressService.getModuleProgress(studentId, moduloId);
        boolean alreadyPassed = progressBefore.isExamPassed();

        int pointsEarned = 0;
        boolean levelUp = false;
        String newLevel = null;
        boolean topicCompleted = false;

        // Solo dar puntos la primera vez que aprueba el examen.
        if (!alreadyPassed) {
            pointsEarned = 100;
            estudiante.setPuntosTotales(estudiante.getPuntosTotales() + 100);
        }

        estudianteRepository.save(estudiante);
        progressService.updateExamProgress(studentId, moduloId, score, true, pointsEarned);
        ModuleProgressResponse updatedProgress =
                progressService.getModuleProgress(studentId, moduloId);

        boolean moduleCompleted = updatedProgress.getProgressPercentage() >= 100;
        boolean alreadyCompleted = estudiante.getModulosCompletados().stream()
                .anyMatch(m -> m.getId().equals(moduloId));

        if (moduleCompleted && !alreadyCompleted) {
            estudiante.getModulosCompletados().add(modulo);
            String nivelActual = estudiante.getNivelDiagnostico();

            if ("BASICO".equals(modulo.getNivelRequerido()) && "BASICO".equals(nivelActual)) {
                estudiante.setNivelDiagnostico("INTERMEDIO");
                levelUp = true;
                newLevel = "I";
            } else if ("INTERMEDIO".equals(modulo.getNivelRequerido())
                    && "INTERMEDIO".equals(nivelActual)) {
                estudiante.setNivelDiagnostico("AVANZADO");
                levelUp = true;
                newLevel = "A";
            }

            estudianteRepository.save(estudiante);
        }

        if (moduleCompleted && modulo.getTema() != null && modulo.getTema().getId() != null) {
            long nivelesCompletados = estudiante.getModulosCompletados().stream()
                    .filter(m -> m.getTema() != null
                            && m.getTema().getId() != null
                            && m.getTema().getId().equals(modulo.getTema().getId()))
                    .count();
            topicCompleted = nivelesCompletados == 3;
        }

        String mensaje = alreadyPassed ?
                "¡Buen repaso! Ya habías aprobado este examen." :
                "¡Felicidades! Has ganado 100 puntos.";

        return new SubmitExamResponse(true, mensaje, pointsEarned, levelUp, newLevel, topicCompleted);
    }
}
