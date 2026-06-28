package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminSummaryResponse {

    private final long totalStudents;
    private final long activeStudents;
    private final long inactiveStudents;
    private final long totalModules;
    private final Integer averageProgress;

    public AdminSummaryResponse(long totalStudents, long activeStudents,
                                long inactiveStudents, long totalModules,
                                Integer averageProgress) {
        this.totalStudents = totalStudents;
        this.activeStudents = activeStudents;
        this.inactiveStudents = inactiveStudents;
        this.totalModules = totalModules;
        this.averageProgress = averageProgress;
    }

    @JsonProperty("total_students")
    public long getTotalStudents() {
        return totalStudents;
    }

    @JsonProperty("active_students")
    public long getActiveStudents() {
        return activeStudents;
    }

    @JsonProperty("inactive_students")
    public long getInactiveStudents() {
        return inactiveStudents;
    }

    @JsonProperty("total_modules")
    public long getTotalModules() {
        return totalModules;
    }

    @JsonProperty("average_progress")
    public Integer getAverageProgress() {
        return averageProgress;
    }
}
