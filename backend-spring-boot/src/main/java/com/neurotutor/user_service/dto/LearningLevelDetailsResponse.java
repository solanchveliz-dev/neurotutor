package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LearningLevelDetailsResponse {
    private final Long id;
    private final Long moduleId;
    private final String title;
    private final String level;
    private final String description;
    private final long lessonsCount;
    private final long practiceCount;
    private final long examCount;

    public LearningLevelDetailsResponse(Long id, Long moduleId, String title, String level,
                                        String description, long lessonsCount,
                                        long practiceCount, long examCount) {
        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.level = level;
        this.description = description;
        this.lessonsCount = lessonsCount;
        this.practiceCount = practiceCount;
        this.examCount = examCount;
    }

    public Long getId() { return id; }

    @JsonProperty("module_id")
    public Long getModuleId() { return moduleId; }

    public String getTitle() { return title; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }

    @JsonProperty("lessons_count")
    public long getLessonsCount() { return lessonsCount; }

    @JsonProperty("practice_count")
    public long getPracticeCount() { return practiceCount; }

    @JsonProperty("exam_count")
    public long getExamCount() { return examCount; }
}
