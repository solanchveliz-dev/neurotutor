package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.DiagnosticRequest;
import com.neurotutor.user_service.dto.DiagnosticResponse;
import com.neurotutor.user_service.dto.ModuleItem;
import com.neurotutor.user_service.dto.StudentProfileResponse;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.model.Modulo;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    /**
     * 🚀 HU-11: Procesa el examen, calcula el nivel y guarda los resultados.
     * 🚀 HU-10: Marca el examen como completado para evitar repeticiones.
     */
    public DiagnosticResponse calcularYGuardarNivel(DiagnosticRequest request) {

        // 📝 Plantilla oficial sincronizada con DiagnosticScreen.kt en Android
        String[] plantillaCorrectas = {"C", "C", "C", "B", "C", "B", "C", "B", "C", "D"};

        List<String> respuestasAlumno = request.getRespuestas();

        // 🛡️ Seguridad: Validar que la lista no sea nula o vacía
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

        // 📊 Asignación de nivel basada en el porcentaje de aciertos (HU-11)
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

        // 💾 Persistencia en MySQL
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
                    estadoModulo
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
}