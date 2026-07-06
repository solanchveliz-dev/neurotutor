package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.DiagnosticAnswer;
import com.neurotutor.user_service.model.DiagnosticAttempt;
import com.neurotutor.user_service.model.DiagnosticQuestion;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.model.Modulo;
import com.neurotutor.user_service.repository.DiagnosticAnswerRepository;
import com.neurotutor.user_service.repository.DiagnosticAttemptRepository;
import com.neurotutor.user_service.repository.DiagnosticQuestionRepository;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DiagnosticService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private DiagnosticQuestionRepository diagnosticQuestionRepository;

    @Autowired
    private DiagnosticAttemptRepository diagnosticAttemptRepository;

    @Autowired
    private DiagnosticAnswerRepository diagnosticAnswerRepository;

    @Autowired
    private AchievementService achievementService;

    /**
     *  HU-11: Procesa el examen, calcula el nivel y guarda los resultados.
     *  HU-10: Marca el examen como completado para evitar repeticiones.
     */
    public DiagnosticResponse calcularYGuardarNivel(DiagnosticRequest request) {

        //  Plantilla oficial sincronizada con DiagnosticScreen.kt en Android
        String[] plantillaCorrectas = {"D", "C", "C", "B", "C", "B", "C", "B", "A", "D"};

        List<String> respuestasAlumno = request.getRespuestas();

        // 🛡 Seguridad: Validar que la lista no sea nula o vacía
        if (respuestasAlumno == null || respuestasAlumno.isEmpty()) {
            throw new RuntimeException("El examen no contiene respuestas válidas.");
        }

        int correctas = 0;
        int totalPreguntas = Math.min(respuestasAlumno.size(), plantillaCorrectas.length);

        for (int i = 0; i < totalPreguntas; i++) {
            String respuestaEstudiante = respuestasAlumno.get(i).trim();
            String respuestaCorrectaOficial = plantillaCorrectas[i];

            if (respuestaCorrectaOficial.equalsIgnoreCase(respuestaEstudiante)) {
                correctas++;
            }
        }

        //  Asignación de nivel basada en el porcentaje de aciertos (HU-11)
        String nivel;
        String mensaje;
        if (correctas >= 8) {
            nivel = "AVANZADO";
            mensaje = "¡Increíble! Tu nivel es Avanzado. 🚀";
        } else if (correctas >= 5) {
            nivel = "INTERMEDIO";
            mensaje = "¡Muy bien! Tu nivel es Intermedio. 🔥";
        } else {
            nivel = "BASICO";
            mensaje = "¡Buen intento! Vamos a empezar desde las bases. 🌱";
        }

        //  Persistencia en MySQL
        Long idEstudiante = Long.parseLong(request.getStudentId());
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setNivelDiagnostico(nivel);
        estudiante.setExamenCompletado(true); // ✅ Criterio HU-10: Evita bucle del examen

        estudianteRepository.save(estudiante);

        return new DiagnosticResponse(nivel, mensaje);
    }

    /**
     * 🚀 HU-14: Obtiene los datos dinámicos para el Dashboard.
     * Filtra los módulos según el nivel calculado.
     */
    public StudentProfileResponse obtenerDatosDashboard(Long studentId) {
        // 1. Buscamos el alumno real en la DB
        Estudiante estudiante = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // 🛡️ SEGURIDAD: Si por algún motivo el nivel es null, asignamos BASICO por defecto
        String nivelActual = estudiante.getNivelDiagnostico();
        if (nivelActual == null || nivelActual.isEmpty()) {
            nivelActual = "BASICO";
        }

        // 2. Traemos módulos filtrados por el nivel del estudiante (HU-14)
        List<Modulo> modulosDelNivel = moduloRepository.findByNivelRequerido(nivelActual);

        // 3. Mapeamos los datos al DTO que Android espera
        List<ModuleItem> listaModulosDTO = new ArrayList<>();
        for (int i = 0; i < modulosDelNivel.size(); i++) {
            Modulo m = modulosDelNivel.get(i);

            // Primer módulo "EN_CURSO", los demás "BLOQUEADO"
            String estadoModulo = (i == 0) ? "EN_CURSO" : "BLOQUEADO";

            listaModulosDTO.add(new ModuleItem(
                    m.getId().toString(),
                    m.getTitulo(),
                    0, // ejerciciosCompletados (inicial)
                    m.getEjerciciosTotales(),
                    estadoModulo,
                    m.getTema().getNombre(),
                    m.getNivelRequerido()
            ));
        }

        String gradoSeccionCompleto = estudiante.getGrado() + " " + estudiante.getSeccion();

        // 4. Retornamos el perfil completo con puntos reales (HU-14)
        return new StudentProfileResponse(
                estudiante.getNombreCompleto(),
                gradoSeccionCompleto,
                nivelActual,
                estudiante.getPuntosTotales(), // ✅ Valor real desde MySQL
                listaModulosDTO
        );
    }
    @Transactional(readOnly = true)
    public List<DiagnosticQuestionResponse> getActiveQuestions() {
        return diagnosticQuestionRepository.findByActiveTrueOrderByOrderNumberAsc().stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Transactional
    public DiagnosticResultResponse submitDiagnosticV2(SubmitDiagnosticV2Request request) {
        validateSubmitDiagnosticV2Request(request);

        Estudiante estudiante = estudianteRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Map<Long, DiagnosticQuestion> questionById = diagnosticQuestionRepository.findByActiveTrueOrderByOrderNumberAsc()
                .stream()
                .collect(Collectors.toMap(DiagnosticQuestion::getId, Function.identity()));

        int totalQuestions = request.getAnswers().size();
        int correctAnswers = 0;
        List<DiagnosticAnswer> answersToSave = new ArrayList<>();

        DiagnosticAttempt attempt = new DiagnosticAttempt();
        attempt.setStudent(estudiante);
        attempt.setCompletedAt(LocalDateTime.now());
        DiagnosticAttempt savedAttempt = diagnosticAttemptRepository.save(attempt);

        for (SubmitDiagnosticV2Request.DiagnosticAnswerRequest answerRequest : request.getAnswers()) {
            DiagnosticQuestion question = questionById.get(answerRequest.getQuestionId());
            if (question == null) {
                throw new RuntimeException("Pregunta diagnostica no encontrada o inactiva");
            }

            boolean correct = question.getCorrectAnswerIndex() == answerRequest.getSelectedAnswerIndex();
            if (correct) {
                correctAnswers++;
            }

            DiagnosticAnswer diagnosticAnswer = new DiagnosticAnswer();
            diagnosticAnswer.setAttempt(savedAttempt);
            diagnosticAnswer.setQuestion(question);
            diagnosticAnswer.setSelectedAnswerIndex(answerRequest.getSelectedAnswerIndex());
            diagnosticAnswer.setCorrect(correct);
            answersToSave.add(diagnosticAnswer);
        }

        int scorePercentage = Math.round((correctAnswers * 100f) / totalQuestions);
        String assignedLevel = calculateLevelByPercentage(scorePercentage);
        String message = buildLevelMessage(assignedLevel);

        savedAttempt.setTotalQuestions(totalQuestions);
        savedAttempt.setCorrectAnswers(correctAnswers);
        savedAttempt.setScorePercentage(scorePercentage);
        savedAttempt.setAssignedLevel(assignedLevel);
        diagnosticAttemptRepository.save(savedAttempt);
        diagnosticAnswerRepository.saveAll(answersToSave);

        estudiante.setNivelDiagnostico(assignedLevel);
        estudiante.setExamenCompletado(true);
        estudianteRepository.save(estudiante);
        achievementService.evaluateStudentAchievements(estudiante.getId());

        return new DiagnosticResultResponse(
                savedAttempt.getId(),
                estudiante.getId(),
                correctAnswers,
                totalQuestions,
                scorePercentage,
                assignedLevel,
                message
        );
    }

    @Transactional(readOnly = true)
    public DiagnosticReviewResponse getDiagnosticReview(Long attemptId) {
        DiagnosticAttempt attempt = diagnosticAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Intento diagnostico no encontrado"));
        return toReviewResponse(attempt);
    }

    @Transactional(readOnly = true)
    public DiagnosticReviewResponse getLatestDiagnosticReview(Long studentId) {
        DiagnosticAttempt attempt = diagnosticAttemptRepository.findTopByStudentIdOrderByCompletedAtDesc(studentId)
                .orElseThrow(() -> new RuntimeException("Intento diagnostico no encontrado"));
        return toReviewResponse(attempt);
    }

    private void validateSubmitDiagnosticV2Request(SubmitDiagnosticV2Request request) {
        if (request == null) {
            throw new RuntimeException("La solicitud de diagnostico es obligatoria");
        }
        if (request.getStudentId() == null) {
            throw new RuntimeException("student_id es obligatorio");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("answers es obligatorio");
        }
    }

    private DiagnosticQuestionResponse toQuestionResponse(DiagnosticQuestion question) {
        return new DiagnosticQuestionResponse(
                question.getId(),
                question.getTextBeforeImage(),
                question.getTextAfterImage(),
                question.getImageUrl(),
                question.getOptions(),
                question.getTopic(),
                question.getOrderNumber()
        );
    }

    private DiagnosticReviewResponse toReviewResponse(DiagnosticAttempt attempt) {
        List<DiagnosticReviewResponse.QuestionReview> questionReviews = diagnosticAnswerRepository
                .findByAttemptIdOrderByQuestionOrderNumberAsc(attempt.getId())
                .stream()
                .map(answer -> {
                    DiagnosticQuestion question = answer.getQuestion();
                    return new DiagnosticReviewResponse.QuestionReview(
                            question.getId(),
                            question.getTopic(),
                            question.getTextBeforeImage(),
                            question.getTextAfterImage(),
                            question.getImageUrl(),
                            question.getOptions(),
                            answer.getSelectedAnswerIndex(),
                            question.getCorrectAnswerIndex(),
                            answer.isCorrect(),
                            question.getExplanation()
                    );
                })
                .toList();

        return new DiagnosticReviewResponse(
                attempt.getId(),
                attempt.getStudent().getId(),
                attempt.getAssignedLevel(),
                attempt.getCorrectAnswers(),
                attempt.getTotalQuestions(),
                attempt.getScorePercentage(),
                questionReviews
        );
    }

    private String calculateLevelByPercentage(int scorePercentage) {
        if (scorePercentage <= 40) {
            return "BASICO";
        }
        if (scorePercentage <= 75) {
            return "INTERMEDIO";
        }
        return "AVANZADO";
    }

    private String buildLevelMessage(String level) {
        if ("AVANZADO".equals(level)) {
            return "¡Increíble! Tu nivel es Avanzado.";
        }
        if ("INTERMEDIO".equals(level)) {
            return "¡Muy bien! Tu nivel es Intermedio.";
        }
        return "¡Buen intento! Vamos a empezar desde las bases.";
    }
}
