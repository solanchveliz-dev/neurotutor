package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.AiErrorResponse;
import com.neurotutor.user_service.dto.AiTutorRequest;
import com.neurotutor.user_service.dto.AiTutorResponse;
import com.neurotutor.user_service.service.AiService;
import com.neurotutor.user_service.service.ChatHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {
    private final AiService aiService;
    private final ChatHistoryService chatHistoryService;

    public AiController(AiService aiService, ChatHistoryService chatHistoryService) {
        this.aiService = aiService;
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/tutor")
    @PreAuthorize("#request.studentId != null && #request.studentId.toString() == authentication.name")
    public ResponseEntity<?> askTutor(@RequestBody AiTutorRequest request) {
        try {
            AiTutorResponse response = aiService.askTutor(request);
            chatHistoryService.recordSuccessfulExchange(request, response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new AiErrorResponse(exception.getMessage()));
        } catch (IllegalStateException exception) {
            HttpStatus status = exception.getMessage() != null && exception.getMessage().contains("GROQ_API_KEY")
                    ? HttpStatus.SERVICE_UNAVAILABLE
                    : HttpStatus.BAD_GATEWAY;
            return ResponseEntity.status(status).body(new AiErrorResponse(exception.getMessage()));
        }
    }
}
