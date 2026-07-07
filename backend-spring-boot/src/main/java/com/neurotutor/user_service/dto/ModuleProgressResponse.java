package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ModuleProgressResponse {
    private Long moduleId;
    private String title;
    private String level;
    private boolean theoryCompleted;
    private boolean practiceCompleted;
    private boolean examPassed;
    private int practiceCompletedCount;
    private int practiceTotalCount;
    private int examBestScore;
    private int progressPercentage;
    private LocalDateTime lastActivityAt;
    private LocalDateTime completedAt;

    public ModuleProgressResponse(Long moduleId, String title, String level,
                                  boolean theoryCompleted, boolean practiceCompleted,
                                  boolean examPassed, int practiceCompletedCount,
                                  int practiceTotalCount, int examBestScore,
                                  int progressPercentage, LocalDateTime lastActivityAt,
                                  LocalDateTime completedAt) {
        this.moduleId = moduleId;
        this.title = title;
        this.level = level;
        this.theoryCompleted = theoryCompleted;
        this.practiceCompleted = practiceCompleted;
        this.examPassed = examPassed;
        this.practiceCompletedCount = practiceCompletedCount;
        this.practiceTotalCount = practiceTotalCount;
        this.examBestScore = examBestScore;
        this.progressPercentage = progressPercentage;
        this.lastActivityAt = lastActivityAt;
        this.completedAt = completedAt;
    }

    @JsonProperty("module_id")
    public Long getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }

    @JsonProperty("theory_completed")
    public boolean isTheoryCompleted() {
        return theoryCompleted;
    }

    @JsonProperty("practice_completed")
    public boolean isPracticeCompleted() {
        return practiceCompleted;
    }

    @JsonProperty("exam_passed")
    public boolean isExamPassed() {
        return examPassed;
    }

    @JsonProperty("practice_completed_count")
    public int getPracticeCompletedCount() {
        return practiceCompletedCount;
    }

    @JsonProperty("practice_total_count")
    public int getPracticeTotalCount() {
        return practiceTotalCount;
    }

    @JsonProperty("exam_best_score")
    public int getExamBestScore() {
        return examBestScore;
    }

    @JsonProperty("progress_percentage")
    public int getProgressPercentage() {
        return progressPercentage;
    }

    @JsonProperty("last_activity_at")
    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    @JsonProperty("completed_at")
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
