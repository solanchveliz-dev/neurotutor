package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String grade;
    private String section;
    private String level;
    private int points;
    private String avatarUrl;
    private String gender;
    private boolean diagnosticCompleted;
    private LocalDateTime createdAt;

    public ProfileResponse(Long id, String name, String email, String grade,
                           String section, String level, int points,
                           String avatarUrl, String gender,
                           boolean diagnosticCompleted, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.grade = grade;
        this.section = section;
        this.level = level;
        this.points = points;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.diagnosticCompleted = diagnosticCompleted;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGrade() { return grade; }
    public String getSection() { return section; }
    public String getLevel() { return level; }
    public int getPoints() { return points; }

    @JsonProperty("avatar_url")
    public String getAvatarUrl() { return avatarUrl; }

    public String getGender() { return gender; }

    @JsonProperty("diagnostic_completed")
    public boolean isDiagnosticCompleted() { return diagnosticCompleted; }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() { return createdAt; }
}
