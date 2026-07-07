package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByOrderByTimestampDesc();
    List<ChatMessage> findByStudent_IdOrderByTimestampAsc(Long studentId);
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(String conversationId);
    void deleteByConversationId(String conversationId);
    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
