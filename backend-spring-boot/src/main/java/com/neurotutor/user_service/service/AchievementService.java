package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AchievementResponse;
import com.neurotutor.user_service.dto.StudentAchievementsResponse;
import com.neurotutor.user_service.model.Achievement;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.model.StudentAchievement;
import com.neurotutor.user_service.model.StudentModuleProgress;
import com.neurotutor.user_service.repository.AchievementRepository;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.StudentAchievementRepository;
import com.neurotutor.user_service.repository.StudentModuleProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AchievementService {
    public static final String DIAGNOSTIC_COMPLETED = "DIAGNOSTIC_COMPLETED";
    public static final String FIRST_THEORY_COMPLETED = "FIRST_THEORY_COMPLETED";
    public static final String FIRST_PRACTICE_PASSED = "FIRST_PRACTICE_PASSED";
    public static final String FIRST_EXAM_PASSED = "FIRST_EXAM_PASSED";
    public static final String FIRST_MODULE_COMPLETED = "FIRST_MODULE_COMPLETED";
    public static final String POINTS_100 = "POINTS_100";

    private final AchievementRepository achievementRepository;
    private final StudentAchievementRepository studentAchievementRepository;
    private final EstudianteRepository estudianteRepository;
    private final StudentModuleProgressRepository progressRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              StudentAchievementRepository studentAchievementRepository,
                              EstudianteRepository estudianteRepository,
                              StudentModuleProgressRepository progressRepository) {
        this.achievementRepository = achievementRepository;
        this.studentAchievementRepository = studentAchievementRepository;
        this.estudianteRepository = estudianteRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional
    public StudentAchievementsResponse getStudentAchievements(Long studentId) {
        evaluateStudentAchievements(studentId);

        List<StudentAchievement> studentAchievements =
                studentAchievementRepository.findByStudentIdOrderByUnlockedAtDesc(studentId);
        Map<Long, StudentAchievement> unlockedByAchievementId = studentAchievements.stream()
                .collect(Collectors.toMap(item -> item.getAchievement().getId(), Function.identity()));

        List<Achievement> activeAchievements = achievementRepository.findByActiveTrueOrderByIdAsc();
        List<AchievementResponse> unlocked = studentAchievements.stream()
                .filter(item -> item.getAchievement().isActive())
                .map(item -> toResponse(item.getAchievement(), item))
                .toList();
        List<AchievementResponse> locked = activeAchievements.stream()
                .filter(item -> !unlockedByAchievementId.containsKey(item.getId()))
                .map(item -> toResponse(item, null))
                .toList();

        return new StudentAchievementsResponse(unlocked, locked);
    }

    @Transactional
    public void evaluateStudentAchievements(Long studentId) {
        Estudiante student = estudianteRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        List<StudentModuleProgress> progressItems = progressRepository.findByStudentId(studentId);

        if (student.isExamenCompletado()) unlock(student, DIAGNOSTIC_COMPLETED);
        if (progressItems.stream().anyMatch(StudentModuleProgress::isTheoryCompleted)) {
            unlock(student, FIRST_THEORY_COMPLETED);
        }
        if (progressItems.stream().anyMatch(StudentModuleProgress::isPracticeCompleted)) {
            unlock(student, FIRST_PRACTICE_PASSED);
        }
        if (progressItems.stream().anyMatch(StudentModuleProgress::isExamPassed)) {
            unlock(student, FIRST_EXAM_PASSED);
        }
        if (progressItems.stream().anyMatch(item -> item.getProgressPercentage() >= 100)) {
            unlock(student, FIRST_MODULE_COMPLETED);
        }
        if (student.getPuntosTotales() >= 100) unlock(student, POINTS_100);
    }

    private void unlock(Estudiante student, String code) {
        Achievement achievement = achievementRepository.findByCode(code).orElse(null);
        if (achievement == null || !achievement.isActive()
                || studentAchievementRepository.existsByStudentIdAndAchievementId(student.getId(), achievement.getId())) {
            return;
        }

        StudentAchievement studentAchievement = new StudentAchievement();
        studentAchievement.setStudent(student);
        studentAchievement.setAchievement(achievement);
        studentAchievementRepository.save(studentAchievement);
    }

    private AchievementResponse toResponse(Achievement achievement, StudentAchievement unlocked) {
        return new AchievementResponse(
                achievement.getId(), achievement.getCode(), achievement.getTitle(),
                achievement.getDescription(), achievement.getIcon(), achievement.getCategory(),
                achievement.getPointsRequired(), unlocked == null ? null : unlocked.getUnlockedAt()
        );
    }
}
