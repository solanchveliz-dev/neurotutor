package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStudentResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String grade;
    private final String section;
    private final String level;
    private final int points;
    private final String status;
    private final Integer overallProgress;
    private final String diagnosticLevel;
    private final List<ModuleProgressResponse> modulesProgress;
    private final List<ModuleProgressResponse> completedModules;
    private final LocalDateTime lastActivityAt;

    public AdminStudentResponse(Long id, String name, String email, String grade,
                                String section, String level, int points, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.grade = grade;
        this.section = section;
        this.level = level;
        this.points = points;
        this.status = status;
        this.overallProgress = null;
        this.diagnosticLevel = null;
        this.modulesProgress = null;
        this.completedModules = null;
        this.lastActivityAt = null;
    }

    public AdminStudentResponse(Long id, String name, String email, String grade,
                                String section, String level, int points, String status,
                                int overallProgress, String diagnosticLevel,
                                List<ModuleProgressResponse> modulesProgress,
                                List<ModuleProgressResponse> completedModules,
                                LocalDateTime lastActivityAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.grade = grade;
        this.section = section;
        this.level = level;
        this.points = points;
        this.status = status;
        this.overallProgress = overallProgress;
        this.diagnosticLevel = diagnosticLevel;
        this.modulesProgress = modulesProgress;
        this.completedModules = completedModules;
        this.lastActivityAt = lastActivityAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGrade() {
        return grade;
    }

    public String getSection() {
        return section;
    }

    public String getLevel() {
        return level;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return status;
    }

    @JsonProperty("overall_progress")
    public Integer getOverallProgress() {
        return overallProgress;
    }

    @JsonProperty("diagnostic_level")
    public String getDiagnosticLevel() {
        return diagnosticLevel;
    }

    @JsonProperty("modules_progress")
    public List<ModuleProgressResponse> getModulesProgress() {
        return modulesProgress;
    }

    @JsonProperty("completed_modules")
    public List<ModuleProgressResponse> getCompletedModules() {
        return completedModules;
    }

    @JsonProperty("last_activity_at")
    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }
}
