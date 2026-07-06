package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AdminStudentResponse;
import com.neurotutor.user_service.dto.AdminSummaryResponse;
import com.neurotutor.user_service.dto.ModuleProgressResponse;
import com.neurotutor.user_service.dto.StudentProgressResponse;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.ModuloRepository;
import com.neurotutor.user_service.repository.StudentModuleProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final EstudianteRepository estudianteRepository;
    private final ModuloRepository moduloRepository;
    private final ProgressService progressService;
    private final StudentModuleProgressRepository studentModuleProgressRepository;

    public AdminService(EstudianteRepository estudianteRepository,
                        ModuloRepository moduloRepository,
                        ProgressService progressService,
                        StudentModuleProgressRepository studentModuleProgressRepository) {
        this.estudianteRepository = estudianteRepository;
        this.moduloRepository = moduloRepository;
        this.progressService = progressService;
        this.studentModuleProgressRepository = studentModuleProgressRepository;
    }

    public AdminSummaryResponse getSummary() {
        LocalDateTime now = LocalDateTime.now();
        List<Estudiante> students = estudianteRepository.findAll();
        long inactiveStudents = students.stream()
                .filter(student -> isInactive(student, now))
                .count();
        long totalStudents = students.size();
        Double averageProgressValue = studentModuleProgressRepository.findAverageProgressPercentage();
        Integer averageProgress = averageProgressValue == null
                ? null
                : Math.round(averageProgressValue.floatValue());

        return new AdminSummaryResponse(
                totalStudents,
                totalStudents - inactiveStudents,
                inactiveStudents,
                moduloRepository.count(),
                averageProgress
        );
    }

    public List<AdminStudentResponse> getStudents() {
        LocalDateTime now = LocalDateTime.now();
        return estudianteRepository.findAll().stream()
                .map(student -> toResponse(student, now))
                .toList();
    }

    public AdminStudentResponse getStudent(Long id) {
        Estudiante student = estudianteRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
        StudentProgressResponse progress = progressService.getStudentProgress(id);
        List<ModuleProgressResponse> modules = progress.getModules();
        List<ModuleProgressResponse> completedModules = modules.stream()
                .filter(module -> module.getCompletedAt() != null)
                .toList();
        LocalDateTime lastActivityAt = modules.stream()
                .map(ModuleProgressResponse::getLastActivityAt)
                .filter(activityAt -> activityAt != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new AdminStudentResponse(
                student.getId(),
                student.getNombreCompleto(),
                student.getEmail(),
                student.getGrado(),
                student.getSeccion(),
                student.getNivelDiagnostico(),
                progress.getPoints(),
                isInactive(student, LocalDateTime.now()) ? "inactive" : "active",
                progress.getOverallProgress(),
                student.getNivelDiagnostico(),
                modules,
                completedModules,
                lastActivityAt
        );
    }

    private AdminStudentResponse toResponse(Estudiante student, LocalDateTime now) {
        return new AdminStudentResponse(
                student.getId(),
                student.getNombreCompleto(),
                student.getEmail(),
                student.getGrado(),
                student.getSeccion(),
                student.getNivelDiagnostico(),
                student.getPuntosTotales(),
                isInactive(student, now) ? "inactive" : "active"
        );
    }

    private boolean isInactive(Estudiante student, LocalDateTime now) {
        return student.getBloqueadoHasta() != null && student.getBloqueadoHasta().isAfter(now);
    }

    public static class StudentNotFoundException extends RuntimeException {
    }
}
