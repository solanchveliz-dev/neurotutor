package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentProgressResponse {
    private Long studentId;
    private int overallProgress;
    private int points;
    private int streakDays;
    private List<ModuleProgressResponse> modules;

    public StudentProgressResponse(Long studentId, int overallProgress, int points,
                                   int streakDays, List<ModuleProgressResponse> modules) {
        this.studentId = studentId;
        this.overallProgress = overallProgress;
        this.points = points;
        this.streakDays = streakDays;
        this.modules = modules;
    }

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("overall_progress")
    public int getOverallProgress() {
        return overallProgress;
    }

    public int getPoints() {
        return points;
    }

    @JsonProperty("streak_days")
    public int getStreakDays() {
        return streakDays;
    }

    public List<ModuleProgressResponse> getModules() {
        return modules;
    }
}
