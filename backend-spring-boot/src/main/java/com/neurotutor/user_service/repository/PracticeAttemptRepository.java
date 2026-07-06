package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.PracticeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeAttemptRepository extends JpaRepository<PracticeAttempt, Long> {
    List<PracticeAttempt> findByStudentIdAndModuloIdOrderByCompletedAtDesc(Long studentId, Long moduloId);
}
