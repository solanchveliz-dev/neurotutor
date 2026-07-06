package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProfileRequest {
    private String name;
    private String grade;
    private String section;
    private String avatarUrl;
    private String gender;

    public UpdateProfileRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    @JsonProperty("avatar_url")
    public String getAvatarUrl() { return avatarUrl; }

    @JsonProperty("avatar_url")
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
