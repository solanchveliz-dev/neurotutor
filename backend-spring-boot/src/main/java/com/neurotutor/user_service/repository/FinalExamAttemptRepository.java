package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.FinalExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalExamAttemptRepository extends JpaRepository<FinalExamAttempt, Long> {
}
