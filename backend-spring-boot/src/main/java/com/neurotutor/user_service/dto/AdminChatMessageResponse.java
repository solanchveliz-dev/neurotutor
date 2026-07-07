package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminChatMessageResponse {
    private final Long id;
    private final Long studentId;
    private final String studentName;
    private final String studentEmail;
    private final String studentLevel;
    private final String role;
    private final String message;
    private final String sessionId;
    private final String conversationId;
    private final Long moduleId;
    private final LocalDate date;
    private final LocalTime time;
    private final LocalDateTime timestamp;

    public AdminChatMessageResponse(Long id, Long studentId, String studentName, String studentEmail,
                                    String studentLevel, String role, String message, String sessionId,
                                    String conversationId, Long moduleId, LocalDateTime timestamp) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentLevel = studentLevel;
        this.role = role;
        this.message = message;
        this.sessionId = sessionId;
        this.conversationId = conversationId;
        this.moduleId = moduleId;
        this.timestamp = timestamp;
        this.date = timestamp == null ? null : timestamp.toLocalDate();
        this.time = timestamp == null ? null : timestamp.toLocalTime();
    }

    public Long getId() { return id; }
    @JsonProperty("student_id")
    public Long getStudentId() { return studentId; }
    @JsonProperty("student_name")
    public String getStudentName() { return studentName; }
    @JsonProperty("student_email")
    public String getStudentEmail() { return studentEmail; }
    @JsonProperty("student_level")
    public String getStudentLevel() { return studentLevel; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
    @JsonProperty("session_id")
    public String getSessionId() { return sessionId; }
    @JsonProperty("conversation_id")
    public String getConversationId() { return conversationId; }
    @JsonProperty("module_id")
    public Long getModuleId() { return moduleId; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
