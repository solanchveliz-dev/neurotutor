package com.neurotutor.user_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements", uniqueConstraints = @UniqueConstraint(name = "uk_achievement_code", columnNames = "code"))
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 80)
    private String icon;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(name = "points_required")
    private Integer pointsRequired;

    private boolean active = true;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(Integer pointsRequired) { this.pointsRequired = pointsRequired; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
