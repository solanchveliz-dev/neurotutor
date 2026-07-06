package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FinalExamQuestionResponse {
    private final Long id;
    private final String question;
    private final String imageUrl;
    private final List<String> options;

    public FinalExamQuestionResponse(Long id, String question, String imageUrl, List<String> options) {
        this.id = id;
        this.question = question;
        this.imageUrl = imageUrl;
        this.options = options;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    @JsonProperty("image_url")
    public String getImageUrl() { return imageUrl; }
    public List<String> getOptions() { return options; }
}
