package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.ProfileResponse;
import com.neurotutor.user_service.dto.UpdateProfileRequest;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final EstudianteRepository estudianteRepository;

    public ProfileService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long studentId) {
        Estudiante student = findStudent(studentId);
        return toResponse(student);
    }

    @Transactional
    public ProfileResponse updateProfile(Long studentId, UpdateProfileRequest request) {
        Estudiante student = findStudent(studentId);

        if (request != null) {
            if (request.getName() != null) {
                String name = request.getName().trim();
                if (name.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
                }
                student.setNombreCompleto(name);
            }
            if (request.getGrade() != null) {
                student.setGrado(toNullableValue(request.getGrade()));
            }
            if (request.getSection() != null) {
                student.setSeccion(toNullableValue(request.getSection()));
            }
            if (request.getAvatarUrl() != null) {
                student.setAvatarUrl(toNullableValue(request.getAvatarUrl()));
            }
            if (request.getGender() != null) {
                student.setGenero(toNullableValue(request.getGender()));
            }
        }

        return toResponse(estudianteRepository.save(student));
    }

    private String toNullableValue(String value) {
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private Estudiante findStudent(Long studentId) {
        return estudianteRepository.findById(studentId)
                .orElseThrow(StudentProfileNotFoundException::new);
    }

    private ProfileResponse toResponse(Estudiante student) {
        return new ProfileResponse(
                student.getId(),
                student.getNombreCompleto(),
                student.getEmail(),
                student.getGrado(),
                student.getSeccion(),
                student.getNivelDiagnostico(),
                student.getPuntosTotales(),
                student.getAvatarUrl(),
                student.getGenero(),
                student.isExamenCompletado(),
                student.getCreatedAt()
        );
    }

    public static class StudentProfileNotFoundException extends RuntimeException {
    }
}
