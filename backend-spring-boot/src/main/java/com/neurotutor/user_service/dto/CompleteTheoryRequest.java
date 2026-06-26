package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompleteTheoryRequest {
    private Long lessonId;

    public CompleteTheoryRequest() {
    }

    public CompleteTheoryRequest(Long lessonId) {
        this.lessonId = lessonId;
    }

    @JsonProperty("lesson_id")
    public Long getLessonId() {
        return lessonId;
    }

    @JsonProperty("lesson_id")
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}
