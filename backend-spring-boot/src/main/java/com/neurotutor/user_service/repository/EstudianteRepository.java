package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByEmail(String email);
    boolean existsByEmail(String email);
}