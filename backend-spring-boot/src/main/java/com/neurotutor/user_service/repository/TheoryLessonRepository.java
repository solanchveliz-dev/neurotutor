package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.TheoryLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheoryLessonRepository extends JpaRepository<TheoryLesson, Long> {
    List<TheoryLesson> findByModuloIdAndActiveTrueOrderByOrderNumberAsc(Long moduloId);
    Optional<TheoryLesson> findByIdAndActiveTrue(Long id);
    Optional<TheoryLesson> findByModuloIdAndOrderNumber(Long moduloId, int orderNumber);
    long countByModuloIdAndActiveTrue(Long moduloId);
}
