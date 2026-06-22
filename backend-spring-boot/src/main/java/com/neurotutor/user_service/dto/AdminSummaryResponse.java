package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminSummaryResponse {

    private final long totalStudents;
    private final long activeStudents;
    private final long inactiveStudents;
    private final long totalModules;

    public AdminSummaryResponse(long totalStudents, long activeStudents,
                                long inactiveStudents, long totalModules) {
        this.totalStudents = totalStudents;
        this.activeStudents = activeStudents;
        this.inactiveStudents = inactiveStudents;
        this.totalModules = totalModules;
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
}
