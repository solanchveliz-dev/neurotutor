package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SubmitFinalExamAttemptResponse {
    private final Long attemptId;
    private final int correctAnswers;
    private final int totalQuestions;
    private final int scorePercentage;
    private final boolean passed;
    private final int pointsEarned;
    private final String message;
    private final int moduleProgress;
    private final String unlockedBadgeId;
    private final List<String> unlockedAchievementCodes;

    public SubmitFinalExamAttemptResponse(Long attemptId, int correctAnswers, int totalQuestions,
                                          int scorePercentage, boolean passed, int pointsEarned,
                                          String message, int moduleProgress, String unlockedBadgeId,
                                          List<String> unlockedAchievementCodes) {
        this.attemptId = attemptId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.scorePercentage = scorePercentage;
        this.passed = passed;
        this.pointsEarned = pointsEarned;
        this.message = message;
        this.moduleProgress = moduleProgress;
        this.unlockedBadgeId = unlockedBadgeId;
        this.unlockedAchievementCodes = unlockedAchievementCodes;
    }

    @JsonProperty("attempt_id")
    public Long getAttemptId() { return attemptId; }
    @JsonProperty("correct_answers")
    public int getCorrectAnswers() { return correctAnswers; }
    @JsonProperty("total_questions")
    public int getTotalQuestions() { return totalQuestions; }
    @JsonProperty("score_percentage")
    public int getScorePercentage() { return scorePercentage; }
    public boolean isPassed() { return passed; }
    @JsonProperty("points_earned")
    public int getPointsEarned() { return pointsEarned; }
    public String getMessage() { return message; }
    @JsonProperty("module_progress")
    public int getModuleProgress() { return moduleProgress; }
    @JsonProperty("unlocked_badge_id")
    public String getUnlockedBadgeId() { return unlockedBadgeId; }
    @JsonProperty("unlocked_achievement_codes")
    public List<String> getUnlockedAchievementCodes() { return unlockedAchievementCodes; }
}
