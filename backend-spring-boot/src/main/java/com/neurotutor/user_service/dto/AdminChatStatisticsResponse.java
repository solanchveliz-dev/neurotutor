package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class AdminChatStatisticsResponse {
    private final long totalConversations;
    private final long totalMessages;
    private final long totalStudentsUsingAi;
    private final double averageMessagesPerStudent;
    private final String topStudentName;
    private final LocalDateTime lastConversationAt;
    private final long queriesToday;
    private final long queriesThisWeek;
    private final List<MetricItem> conversationsByDay;
    private final List<MetricItem> conversationsByLevel;
    private final List<MetricItem> usageByModule;
    private final List<MetricItem> usageByHour;

    public AdminChatStatisticsResponse(long totalConversations, long totalMessages, long totalStudentsUsingAi,
                                       double averageMessagesPerStudent, String topStudentName,
                                       LocalDateTime lastConversationAt, long queriesToday, long queriesThisWeek,
                                       List<MetricItem> conversationsByDay, List<MetricItem> conversationsByLevel,
                                       List<MetricItem> usageByModule, List<MetricItem> usageByHour) {
        this.totalConversations = totalConversations;
        this.totalMessages = totalMessages;
        this.totalStudentsUsingAi = totalStudentsUsingAi;
        this.averageMessagesPerStudent = averageMessagesPerStudent;
        this.topStudentName = topStudentName;
        this.lastConversationAt = lastConversationAt;
        this.queriesToday = queriesToday;
        this.queriesThisWeek = queriesThisWeek;
        this.conversationsByDay = conversationsByDay;
        this.conversationsByLevel = conversationsByLevel;
        this.usageByModule = usageByModule;
        this.usageByHour = usageByHour;
    }

    @JsonProperty("total_conversations")
    public long getTotalConversations() { return totalConversations; }
    @JsonProperty("total_messages")
    public long getTotalMessages() { return totalMessages; }
    @JsonProperty("total_students_using_ai")
    public long getTotalStudentsUsingAi() { return totalStudentsUsingAi; }
    @JsonProperty("average_messages_per_student")
    public double getAverageMessagesPerStudent() { return averageMessagesPerStudent; }
    @JsonProperty("top_student_name")
    public String getTopStudentName() { return topStudentName; }
    @JsonProperty("last_conversation_at")
    public LocalDateTime getLastConversationAt() { return lastConversationAt; }
    @JsonProperty("queries_today")
    public long getQueriesToday() { return queriesToday; }
    @JsonProperty("queries_this_week")
    public long getQueriesThisWeek() { return queriesThisWeek; }
    @JsonProperty("conversations_by_day")
    public List<MetricItem> getConversationsByDay() { return conversationsByDay; }
    @JsonProperty("conversations_by_level")
    public List<MetricItem> getConversationsByLevel() { return conversationsByLevel; }
    @JsonProperty("usage_by_module")
    public List<MetricItem> getUsageByModule() { return usageByModule; }
    @JsonProperty("usage_by_hour")
    public List<MetricItem> getUsageByHour() { return usageByHour; }

    public static class MetricItem {
        private final String label;
        private final long value;

        public MetricItem(String label, long value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public long getValue() { return value; }
    }
}
