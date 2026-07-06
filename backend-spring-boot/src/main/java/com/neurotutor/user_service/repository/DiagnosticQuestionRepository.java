package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.DiagnosticQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticQuestionRepository extends JpaRepository<DiagnosticQuestion, Long> {
    List<DiagnosticQuestion> findByActiveTrueOrderByOrderNumberAsc();
}
