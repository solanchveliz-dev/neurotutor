package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByCode(String code);
    List<Achievement> findByActiveTrueOrderByIdAsc();
}
