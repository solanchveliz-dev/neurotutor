package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.FinalExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalExamAnswerRepository extends JpaRepository<FinalExamAnswer, Long> {
}
