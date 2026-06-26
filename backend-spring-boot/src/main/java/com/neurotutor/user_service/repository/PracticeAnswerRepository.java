package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.PracticeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeAnswerRepository extends JpaRepository<PracticeAnswer, Long> {
    List<PracticeAnswer> findByAttemptId(Long attemptId);
}
