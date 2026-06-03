package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    boolean existsByEmail(String email);

    // 🚀 ASEGÚRATE DE QUE ESTA LÍNEA EXISTA:
    Optional<Estudiante> findByEmail(String email);
}