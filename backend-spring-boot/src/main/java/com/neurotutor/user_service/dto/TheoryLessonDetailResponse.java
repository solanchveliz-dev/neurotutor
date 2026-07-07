package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TheoryLessonDetailResponse {
    private final Long id;
    private final Long moduleId;
    private final String title;
    private final String subtitle;
    private final String summary;
    private final String icon;
    private final String contentHtml;
    private final JsonNode webContentJson;
    private final int orderNumber;

    public TheoryLessonDetailResponse(Long id, Long moduleId, String title, String subtitle,
                                      String summary, String icon, String contentHtml, JsonNode webContentJson,
                                      int orderNumber) {
        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.icon = icon;
        this.contentHtml = contentHtml;
        this.webContentJson = webContentJson;
        this.orderNumber = orderNumber;
    }

    public Long getId() { return id; }
    @JsonProperty("module_id")
    public Long getModuleId() { return moduleId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSummary() { return summary; }
    public String getIcon() { return icon; }
    @JsonProperty("content_html")
    public String getContentHtml() { return contentHtml; }
    @JsonProperty("web_content_json")
    public JsonNode getWebContentJson() { return webContentJson; }
    @JsonProperty("order_number")
    public int getOrderNumber() { return orderNumber; }
}
