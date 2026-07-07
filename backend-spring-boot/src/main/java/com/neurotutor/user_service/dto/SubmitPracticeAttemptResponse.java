package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SubmitPracticeAttemptResponse {
    private Long attemptId;
    private int correctAnswers;
    private int totalQuestions;
    private int scorePercentage;
    private int pointsEarned;
    private boolean practiceCompleted;
    private int moduleProgress;
    private List<String> unlockedAchievementCodes;

    public SubmitPracticeAttemptResponse(Long attemptId, int correctAnswers,
                                         int totalQuestions, int scorePercentage,
                                         int pointsEarned, boolean practiceCompleted,
                                         int moduleProgress, List<String> unlockedAchievementCodes) {
        this.attemptId = attemptId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.scorePercentage = scorePercentage;
        this.pointsEarned = pointsEarned;
        this.practiceCompleted = practiceCompleted;
        this.moduleProgress = moduleProgress;
        this.unlockedAchievementCodes = unlockedAchievementCodes;
    }

    @JsonProperty("attempt_id")
    public Long getAttemptId() {
        return attemptId;
    }

    @JsonProperty("correct_answers")
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    @JsonProperty("total_questions")
    public int getTotalQuestions() {
        return totalQuestions;
    }

    @JsonProperty("score_percentage")
    public int getScorePercentage() {
        return scorePercentage;
    }

    @JsonProperty("points_earned")
    public int getPointsEarned() {
        return pointsEarned;
    }

    @JsonProperty("practice_completed")
    public boolean isPracticeCompleted() {
        return practiceCompleted;
    }

    @JsonProperty("module_progress")
    public int getModuleProgress() {
        return moduleProgress;
    }

    @JsonProperty("unlocked_achievement_codes")
    public List<String> getUnlockedAchievementCodes() { return unlockedAchievementCodes; }
}
