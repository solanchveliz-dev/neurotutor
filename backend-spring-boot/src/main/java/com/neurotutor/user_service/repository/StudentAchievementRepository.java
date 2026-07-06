package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.StudentAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Long> {
    boolean existsByStudentIdAndAchievementId(Long studentId, Long achievementId);
    List<StudentAchievement> findByStudentIdOrderByUnlockedAtDesc(Long studentId);
}
