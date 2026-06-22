package com.neurotutor.user_service.dto;

public class AdminStudentResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String grade;
    private final String section;
    private final String level;
    private final int points;
    private final String status;

    public AdminStudentResponse(Long id, String name, String email, String grade,
                                String section, String level, int points, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.grade = grade;
        this.section = section;
        this.level = level;
        this.points = points;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGrade() {
        return grade;
    }

    public String getSection() {
        return section;
    }

    public String getLevel() {
        return level;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return status;
    }
}
