package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AdminChatConversationResponse {
    private final String conversationId;
    private final Long studentId;
    private final String studentName;
    private final String studentEmail;
    private final String studentLevel;
    private final String studentStatus;
    private final LocalDate date;
    private final LocalTime time;
    private final LocalDateTime lastMessageAt;
    private final int messageCount;
    private final String lastStudentQuestion;
    private final List<AdminChatMessageResponse> messages;

    public AdminChatConversationResponse(String conversationId, Long studentId, String studentName,
                                         String studentEmail, String studentLevel, String studentStatus,
                                         LocalDateTime lastMessageAt, int messageCount,
                                         String lastStudentQuestion, List<AdminChatMessageResponse> messages) {
        this.conversationId = conversationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentLevel = studentLevel;
        this.studentStatus = studentStatus;
        this.lastMessageAt = lastMessageAt;
        this.date = lastMessageAt == null ? null : lastMessageAt.toLocalDate();
        this.time = lastMessageAt == null ? null : lastMessageAt.toLocalTime();
        this.messageCount = messageCount;
        this.lastStudentQuestion = lastStudentQuestion;
        this.messages = messages;
    }

    @JsonProperty("conversation_id")
    public String getConversationId() { return conversationId; }
    @JsonProperty("student_id")
    public Long getStudentId() { return studentId; }
    @JsonProperty("student_name")
    public String getStudentName() { return studentName; }
    @JsonProperty("student_email")
    public String getStudentEmail() { return studentEmail; }
    @JsonProperty("student_level")
    public String getStudentLevel() { return studentLevel; }
    @JsonProperty("student_status")
    public String getStudentStatus() { return studentStatus; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    @JsonProperty("last_message_at")
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    @JsonProperty("message_count")
    public int getMessageCount() { return messageCount; }
    @JsonProperty("last_student_question")
    public String getLastStudentQuestion() { return lastStudentQuestion; }
    public List<AdminChatMessageResponse> getMessages() { return messages; }
}
