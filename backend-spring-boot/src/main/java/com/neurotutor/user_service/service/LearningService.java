package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.*;
import com.neurotutor.user_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

        List<Modulo> niveles = tema.getNiveles();
        List<ModuleItem> ruta = new ArrayList<>();

        for (Modulo m : niveles) {
            String estado = "BLOQUEADO";
            String nivelEstudiante = estudiante.getNivelDiagnostico(); // BASICO, INTERMEDIO, AVANZADO

            // Reglas de la Épica 3:
            if (m.getNivelRequerido().equals("BASICO")) {
                estado = (nivelEstudiante.equals("BASICO")) ? "EN_CURSO" : "COMPLETADO";
            } else if (m.getNivelRequerido().equals("INTERMEDIO")) {
                if (nivelEstudiante.equals("INTERMEDIO")) estado = "EN_CURSO";
                if (nivelEstudiante.equals("AVANZADO")) estado = "COMPLETADO";
            } else if (m.getNivelRequerido().equals("AVANZADO")) {
                if (nivelEstudiante.equals("AVANZADO")) estado = "EN_CURSO";
            }

            ruta.add(new ModuleItem(m.getId().toString(), m.getTitulo(), 0, m.getEjerciciosTotales(), estado));
        }
        return ruta;
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
                        e.getRespuestaCorrectaIndex(), e.getExplicacionTutorIa(), e.getPuntos()))
                .collect(Collectors.toList());
        return new LearningContentResponse(m.getTeoriaHtml(), ejercicios);
    }

    // 📝 HU-23: Cargar Examen
    public List<Exercise> getFinalExam(Long moduloId) {
        return ejercicioRepository.findByModuloIdAndEsExamenFinal(moduloId, true)
                .stream().map(e -> new Exercise(e.getId().toString(), e.getEnunciado(), e.getOpciones(),
                        e.getRespuestaCorrectaIndex(), "", e.getPuntos()))
                .collect(Collectors.toList());
    }

    // 🚀 HU-24: Promoción de nivel automática
    @Transactional
    public String procesarResultadoExamen(Long studentId, Long moduloId, int score) {
        if (score < 70) return "Necesitas 70% para aprobar.";
        Estudiante e = estudianteRepository.findById(studentId).get();
        Modulo m = moduloRepository.findById(moduloId).get();

        if (m.getNivelRequerido().equals("BASICO") && e.getNivelDiagnostico().equals("BASICO")) {
            e.setNivelDiagnostico("INTERMEDIO");
        } else if (m.getNivelRequerido().equals("INTERMEDIO") && e.getNivelDiagnostico().equals("INTERMEDIO")) {
            e.setNivelDiagnostico("AVANZADO");
        }
        estudianteRepository.save(e);
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
        String nivelExamen = request.getLevel(); // "B", "I", "A"
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

        // Verificar si ya había aprobado este examen antes
        boolean alreadyPassed = estudiante.getModulosCompletados().stream()
                .anyMatch(m -> m.getId().equals(moduloId));

        int pointsEarned = 0;
        boolean levelUp = false;
        String newLevel = null;
        boolean topicCompleted = false;

        // 🎯 REGLA: Solo dar puntos la PRIMERA VEZ que aprueba el examen
        if (!alreadyPassed) {
            pointsEarned = 100;
            estudiante.setPuntosTotales(estudiante.getPuntosTotales() + 100);

            // Marcar módulo como completado
            estudiante.getModulosCompletados().add(modulo);

            // Lógica de desbloqueo de nivel global
            String nivelActual = estudiante.getNivelDiagnostico();

            if ("B".equals(nivelExamen) && "BASICO".equals(nivelActual)) {
                estudiante.setNivelDiagnostico("INTERMEDIO");
                levelUp = true;
                newLevel = "I";
            } else if ("I".equals(nivelExamen) && "INTERMEDIO".equals(nivelActual)) {
                estudiante.setNivelDiagnostico("AVANZADO");
                levelUp = true;
                newLevel = "A";
            }

            // Verificar si completó el tema completo (aprobó los 3 niveles)
            if (modulo.getTema() != null && modulo.getTema().getId() != null) {
                long nivelesCompletados = estudiante.getModulosCompletados().stream()
                        .filter(m -> m.getTema() != null &&
                                m.getTema().getId() != null &&
                                m.getTema().getId().equals(modulo.getTema().getId()))
                        .count();

                if (nivelesCompletados == 3) {
                    topicCompleted = true;
                }
            }
        }

        estudianteRepository.save(estudiante);
        progressService.updateExamProgress(studentId, moduloId, score, true, pointsEarned);

        String mensaje = alreadyPassed ?
                "¡Buen repaso! Ya habías aprobado este examen." :
                "¡Felicidades! Has ganado 100 puntos.";

        return new SubmitExamResponse(true, mensaje, pointsEarned, levelUp, newLevel, topicCompleted);
    }
}
