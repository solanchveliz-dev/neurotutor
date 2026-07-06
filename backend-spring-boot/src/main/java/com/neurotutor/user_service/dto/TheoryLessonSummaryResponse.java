package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TheoryLessonSummaryResponse {
    private final Long id;
    private final String title;
    private final String subtitle;
    private final String summary;
    private final String icon;
    private final int orderNumber;

    public TheoryLessonSummaryResponse(Long id, String title, String subtitle,
                                       String summary, String icon, int orderNumber) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.icon = icon;
        this.orderNumber = orderNumber;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSummary() { return summary; }
    public String getIcon() { return icon; }
    @JsonProperty("order_number")
    public int getOrderNumber() { return orderNumber; }
}
