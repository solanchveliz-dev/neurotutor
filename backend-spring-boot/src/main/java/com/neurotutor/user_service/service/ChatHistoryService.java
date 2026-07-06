package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AdminChatConversationResponse;
import com.neurotutor.user_service.dto.AdminChatMessageResponse;
import com.neurotutor.user_service.dto.AdminChatStatisticsResponse;
import com.neurotutor.user_service.dto.AiTutorRequest;
import com.neurotutor.user_service.dto.AiTutorResponse;
import com.neurotutor.user_service.model.ChatMessage;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.ChatMessageRepository;
import com.neurotutor.user_service.repository.EstudianteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChatHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final EstudianteRepository estudianteRepository;

    public ChatHistoryService(ChatMessageRepository chatMessageRepository,
                              EstudianteRepository estudianteRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public void recordSuccessfulExchange(AiTutorRequest request, AiTutorResponse response) {
        if (request == null || request.getStudentId() == null || response == null) {
            return;
        }

        try {
            Optional<Estudiante> studentResult = estudianteRepository.findById(request.getStudentId());
            if (studentResult.isEmpty()) {
                return;
            }

            Estudiante student = studentResult.get();
            String conversationId = firstNotBlank(request.getConversationId(), UUID.randomUUID().toString());
            String sessionId = firstNotBlank(request.getSessionId(), conversationId);

            chatMessageRepository.save(buildMessage(student, request, "student", getEffectiveMessage(request), sessionId, conversationId));
            chatMessageRepository.save(buildMessage(student, request, "assistant", response.getAnswer(), sessionId, conversationId));
        } catch (RuntimeException exception) {
            logger.warn("No se pudo registrar la conversacion del chat IA. studentId={}, message={}",
                    request.getStudentId(), exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<AdminChatConversationResponse> getConversations() {
        List<ChatMessage> messages = chatMessageRepository.findAllByOrderByTimestampDesc();
        return toConversationResponses(messages, false);
    }

    @Transactional(readOnly = true)
    public List<AdminChatMessageResponse> getStudentMessages(Long studentId) {
        return chatMessageRepository.findByStudent_IdOrderByTimestampAsc(studentId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminChatStatisticsResponse getStatistics() {
        List<ChatMessage> messages = chatMessageRepository.findAllByOrderByTimestampDesc();
        List<AdminChatConversationResponse> conversations = toConversationResponses(messages, false);
        long totalStudents = conversations.stream()
                .map(AdminChatConversationResponse::getStudentId)
                .filter(id -> id != null)
                .distinct()
                .count();
        double averageMessages = totalStudents == 0
                ? 0
                : Math.round((messages.size() * 10.0 / totalStudents)) / 10.0;
        String topStudent = messages.stream()
                .collect(Collectors.groupingBy(ChatMessage::getStudentName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
        LocalDateTime lastConversation = conversations.stream()
                .map(AdminChatConversationResponse::getLastMessageAt)
                .filter(value -> value != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDate today = LocalDate.now();
        LocalDateTime startToday = today.atStartOfDay();
        LocalDateTime endToday = today.atTime(LocalTime.MAX);
        LocalDateTime startWeek = today.with(DayOfWeek.MONDAY).atStartOfDay();
        long queriesToday = countStudentMessagesBetween(messages, startToday, endToday);
        long queriesThisWeek = countStudentMessagesBetween(messages, startWeek, LocalDateTime.now());

        return new AdminChatStatisticsResponse(
                conversations.size(),
                messages.size(),
                totalStudents,
                averageMessages,
                topStudent,
                lastConversation,
                queriesToday,
                queriesThisWeek,
                metricBy(conversations, conversation -> String.valueOf(conversation.getDate())),
                metricBy(conversations, conversation -> displayLevel(conversation.getStudentLevel())),
                metricBy(messages, message -> message.getModuleId() == null ? "Sin modulo" : "Modulo " + message.getModuleId()),
                metricBy(messages, message -> message.getTimestamp() == null ? "Sin hora" : String.format("%02d:00", message.getTimestamp().getHour()))
        );
    }

    @Transactional
    public void deleteConversation(String conversationId) {
        chatMessageRepository.deleteByConversationId(conversationId);
    }

    private ChatMessage buildMessage(Estudiante student, AiTutorRequest request, String role,
                                     String text, String sessionId, String conversationId) {
        ChatMessage message = new ChatMessage();
        message.setStudent(student);
        message.setStudentName(firstNotBlank(student.getNombreCompleto(), "Estudiante"));
        message.setStudentEmail(firstNotBlank(student.getEmail(), "Sin correo"));
        message.setStudentLevel(student.getNivelDiagnostico());
        message.setRole(role);
        message.setMessage(limitText(text, 5000));
        message.setSessionId(limitText(sessionId, 80));
        message.setConversationId(limitText(conversationId, 80));
        message.setModuleId(request.getModuleId());
        message.setLevelId(request.getLevelId());
        message.setLessonId(request.getLessonId());
        message.setExerciseId(request.getExerciseId());
        message.setCurrentScreen(limitText(request.getCurrentScreen(), 80));
        message.setAction(limitText(request.getAction(), 80));
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    private List<AdminChatConversationResponse> toConversationResponses(List<ChatMessage> messages, boolean includeMessages) {
        Map<String, List<ChatMessage>> byConversation = messages.stream()
                .collect(Collectors.groupingBy(
                        ChatMessage::getConversationId,
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));

        return byConversation.values().stream()
                .map(group -> {
                    List<ChatMessage> ordered = group.stream()
                            .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                            .toList();
                    ChatMessage first = ordered.get(0);
                    ChatMessage last = ordered.get(ordered.size() - 1);
                    String lastQuestion = ordered.stream()
                            .filter(message -> "student".equalsIgnoreCase(message.getRole()))
                            .reduce((previous, current) -> current)
                            .map(ChatMessage::getMessage)
                            .orElse("");
                    List<AdminChatMessageResponse> messageResponses = includeMessages
                            ? ordered.stream().map(this::toMessageResponse).toList()
                            : List.of();
                    return new AdminChatConversationResponse(
                            first.getConversationId(),
                            first.getStudent().getId(),
                            first.getStudentName(),
                            first.getStudentEmail(),
                            first.getStudentLevel(),
                            isInactive(first.getStudent()) ? "inactive" : "active",
                            last.getTimestamp(),
                            ordered.size(),
                            lastQuestion,
                            messageResponses
                    );
                })
                .sorted(Comparator.comparing(
                        AdminChatConversationResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    private AdminChatMessageResponse toMessageResponse(ChatMessage message) {
        return new AdminChatMessageResponse(
                message.getId(),
                message.getStudent().getId(),
                message.getStudentName(),
                message.getStudentEmail(),
                message.getStudentLevel(),
                message.getRole(),
                message.getMessage(),
                message.getSessionId(),
                message.getConversationId(),
                message.getModuleId(),
                message.getTimestamp()
        );
    }

    private <T> List<AdminChatStatisticsResponse.MetricItem> metricBy(List<T> items, Function<T, String> labelGetter) {
        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> firstNotBlank(labelGetter.apply(item), "Sin datos"),
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new AdminChatStatisticsResponse.MetricItem(entry.getKey(), entry.getValue()))
                .limit(12)
                .toList();
    }

    private boolean isInactive(Estudiante student) {
        return student.getBloqueadoHasta() != null && student.getBloqueadoHasta().isAfter(LocalDateTime.now());
    }

    private long countStudentMessagesBetween(List<ChatMessage> messages, LocalDateTime start, LocalDateTime end) {
        return messages.stream()
                .filter(message -> "student".equalsIgnoreCase(message.getRole()))
                .map(ChatMessage::getTimestamp)
                .filter(timestamp -> timestamp != null && !timestamp.isBefore(start) && !timestamp.isAfter(end))
                .count();
    }

    private String getEffectiveMessage(AiTutorRequest request) {
        if (request.getMessage() != null && !request.getMessage().isBlank()) {
            return request.getMessage().trim();
        }
        return request.getQuestion() == null ? "" : request.getQuestion().trim();
    }

    private String displayLevel(String value) {
        String normalized = firstNotBlank(value, "Sin nivel").toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "BASICO" -> "Basico";
            case "INTERMEDIO" -> "Intermedio";
            case "AVANZADO" -> "Avanzado";
            default -> normalized;
        };
    }

    private String firstNotBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String limitText(String value, int maxLength) {
        String safeValue = value == null ? "" : value.trim();
        return safeValue.length() <= maxLength ? safeValue : safeValue.substring(0, maxLength);
    }
}
