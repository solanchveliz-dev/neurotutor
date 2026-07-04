package com.neurotutor.user_service.config;

import com.neurotutor.user_service.model.Achievement;
import com.neurotutor.user_service.repository.AchievementRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AchievementSeeder implements ApplicationRunner {
    private final AchievementRepository achievementRepository;

    public AchievementSeeder(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<SeedAchievement> seeds = List.of(
                new SeedAchievement("DIAGNOSTIC_COMPLETED", "Primer paso", "Completaste tu diagnóstico inicial.", "clipboard-check", "DIAGNOSTIC", null),
                new SeedAchievement("FIRST_THEORY_COMPLETED", "Mente curiosa", "Completaste tu primera teoría.", "book-open-check", "LEARNING", null),
                new SeedAchievement("FIRST_PRACTICE_PASSED", "Manos a la práctica", "Aprobaste tu primera práctica.", "pencil-ruler", "PRACTICE", null),
                new SeedAchievement("FIRST_EXAM_PASSED", "Examen superado", "Aprobaste tu primer examen final.", "trophy", "EXAM", null),
                new SeedAchievement("FIRST_MODULE_COMPLETED", "Módulo dominado", "Completaste teoría, práctica y examen de un nivel.", "medal", "PROGRESS", null),
                new SeedAchievement("BASIC_LEVEL_COMPLETED", "Nivel Básico completado", "Completaste teoría, práctica y examen del nivel Básico.", "icon_trophy", "PROGRESS", null),
                new SeedAchievement("INTERMEDIATE_LEVEL_COMPLETED", "Nivel Intermedio completado", "Completaste teoría, práctica y examen del nivel Intermedio.", "icon_trophy", "PROGRESS", null),
                new SeedAchievement("ADVANCED_LEVEL_COMPLETED", "Nivel Avanzado completado", "Completaste teoría, práctica y examen del nivel Avanzado.", "icon_trophy", "PROGRESS", null),
                new SeedAchievement("POINTS_100", "Centena brillante", "Alcanzaste 100 puntos en NeuroTutor.", "star", "POINTS", 100)
        );

        for (SeedAchievement seed : seeds) {
            Achievement achievement = achievementRepository.findByCode(seed.code()).orElseGet(Achievement::new);
            achievement.setCode(seed.code());
            achievement.setTitle(seed.title());
            achievement.setDescription(seed.description());
            achievement.setIcon(seed.icon());
            achievement.setCategory(seed.category());
            achievement.setPointsRequired(seed.pointsRequired());
            achievement.setActive(true);
            achievementRepository.save(achievement);
        }
    }

    private record SeedAchievement(String code, String title, String description, String icon,
                                   String category, Integer pointsRequired) { }
}
