package com.neurotutor.user_service.dto;

import java.util.List;

public class StudentAchievementsResponse {
    private final List<AchievementResponse> unlocked;
    private final List<AchievementResponse> locked;

    public StudentAchievementsResponse(List<AchievementResponse> unlocked, List<AchievementResponse> locked) {
        this.unlocked = unlocked;
        this.locked = locked;
    }

    public List<AchievementResponse> getUnlocked() { return unlocked; }
    public List<AchievementResponse> getLocked() { return locked; }
}
