package com.neurotutor.user_service.dto;

import java.util.List;

public class LearningModuleDetailsResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final List<LearningLevelDetailsResponse> levels;

    public LearningModuleDetailsResponse(Long id, String title, String description,
                                         List<LearningLevelDetailsResponse> levels) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.levels = levels;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<LearningLevelDetailsResponse> getLevels() { return levels; }
}
