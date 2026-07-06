package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.DiagnosticAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosticAttemptRepository extends JpaRepository<DiagnosticAttempt, Long> {
    Optional<DiagnosticAttempt> findTopByStudentIdOrderByCompletedAtDesc(Long studentId);
    List<DiagnosticAttempt> findByStudentIdOrderByCompletedAtDesc(Long studentId);
}
