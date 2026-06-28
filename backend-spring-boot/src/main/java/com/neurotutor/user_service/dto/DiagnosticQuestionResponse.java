package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DiagnosticQuestionResponse {
    private Long id;
    private String textBeforeImage;
    private String textAfterImage;
    private String imageUrl;
    private List<String> options;
    private String topic;
    private int order;

    public DiagnosticQuestionResponse(Long id, String textBeforeImage, String textAfterImage,
                                      String imageUrl, List<String> options, String topic,
                                      int order) {
        this.id = id;
        this.textBeforeImage = textBeforeImage;
        this.textAfterImage = textAfterImage;
        this.imageUrl = imageUrl;
        this.options = options;
        this.topic = topic;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    @JsonProperty("text_before_image")
    public String getTextBeforeImage() {
        return textBeforeImage;
    }

    @JsonProperty("text_after_image")
    public String getTextAfterImage() {
        return textAfterImage;
    }

    @JsonProperty("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getTopic() {
        return topic;
    }

    public int getOrder() {
        return order;
    }
}
