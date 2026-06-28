package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.DiagnosticAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticAnswerRepository extends JpaRepository<DiagnosticAnswer, Long> {
    List<DiagnosticAnswer> findByAttemptIdOrderByQuestionOrderNumberAsc(Long attemptId);
}
