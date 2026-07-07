package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class AchievementResponse {
    private final Long id;
    private final String code;
    private final String title;
    private final String description;
    private final String icon;
    private final String category;
    private final Integer pointsRequired;
    private final LocalDateTime unlockedAt;

    public AchievementResponse(Long id, String code, String title, String description, String icon,
                               String category, Integer pointsRequired, LocalDateTime unlockedAt) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.pointsRequired = pointsRequired;
        this.unlockedAt = unlockedAt;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getCategory() { return category; }
    @JsonProperty("points_required") public Integer getPointsRequired() { return pointsRequired; }
    @JsonProperty("unlocked_at") public LocalDateTime getUnlockedAt() { return unlockedAt; }
}
