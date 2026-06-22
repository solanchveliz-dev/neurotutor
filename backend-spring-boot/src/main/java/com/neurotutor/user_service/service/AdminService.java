package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AdminStudentResponse;
import com.neurotutor.user_service.dto.AdminSummaryResponse;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.ModuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final EstudianteRepository estudianteRepository;
    private final ModuloRepository moduloRepository;

    public AdminService(EstudianteRepository estudianteRepository,
                        ModuloRepository moduloRepository) {
        this.estudianteRepository = estudianteRepository;
        this.moduloRepository = moduloRepository;
    }

    public AdminSummaryResponse getSummary() {
        LocalDateTime now = LocalDateTime.now();
        List<Estudiante> students = estudianteRepository.findAll();
        long inactiveStudents = students.stream()
                .filter(student -> isInactive(student, now))
                .count();
        long totalStudents = students.size();

        return new AdminSummaryResponse(
                totalStudents,
                totalStudents - inactiveStudents,
                inactiveStudents,
                moduloRepository.count()
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
        return toResponse(student, LocalDateTime.now());
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
