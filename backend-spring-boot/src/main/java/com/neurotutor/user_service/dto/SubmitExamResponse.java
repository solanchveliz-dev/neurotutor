package com.neurotutor.user_service.dto;

public class SubmitExamResponse {
    private boolean success;
    private String message;
    private int pointsEarned;
    private boolean levelUp;
    private String newLevel;
    private boolean topicCompleted;

    public SubmitExamResponse() {}

    public SubmitExamResponse(boolean success, String message, int pointsEarned,
                              boolean levelUp, String newLevel, boolean topicCompleted) {
        this.success = success;
        this.message = message;
        this.pointsEarned = pointsEarned;
        this.levelUp = levelUp;
        this.newLevel = newLevel;
        this.topicCompleted = topicCompleted;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public boolean isLevelUp() {
        return levelUp;
    }

    public void setLevelUp(boolean levelUp) {
        this.levelUp = levelUp;
    }

    public String getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(String newLevel) {
        this.newLevel = newLevel;
    }

    public boolean isTopicCompleted() {
        return topicCompleted;
    }

    public void setTopicCompleted(boolean topicCompleted) {
        this.topicCompleted = topicCompleted;
    }
}