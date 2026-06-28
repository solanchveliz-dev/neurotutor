package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.StudentAchievementsResponse;
import com.neurotutor.user_service.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/{studentId}/achievements")
    public ResponseEntity<StudentAchievementsResponse> getStudentAchievements(@PathVariable Long studentId) {
        return ResponseEntity.ok(achievementService.getStudentAchievements(studentId));
    }
}
