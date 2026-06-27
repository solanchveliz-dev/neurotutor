package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AiTutorRequest;
import com.neurotutor.user_service.dto.AiTutorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private static final String GROQ_MODEL = "llama-3.1-8b-instant";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_UNAVAILABLE_MESSAGE =
            "Neo IA está temporalmente no disponible. Intenta nuevamente en unos minutos.";

    private static final String SYSTEM_PROMPT = """
            Eres Neo, el Tutor IA de NeuroTutor para estudiantes de matemáticas.
            Tu objetivo es ayudar a aprender, no solo entregar respuestas.
            Reglas:
            - Explica paso a paso con lenguaje claro y breve.
            - Haz preguntas guía cuando sea útil.
            - No des solo la respuesta final.
            - Si el estudiante pregunta algo fuera de aprendizaje o matemáticas, responde amablemente que solo puedes ayudar con temas de aprendizaje matemático.
            - No solicites ni proceses datos personales sensibles.
            - Mantén un tono cercano, educativo y apropiado para estudiantes.
            - No inventes progreso, notas ni actividades que no aparezcan en el contexto recibido.
            """;

    private final RestClient restClient;

    public AiService() {
        this.restClient = RestClient.create();
    }

    public AiTutorResponse askTutor(AiTutorRequest request) {
        validateRequest(request);

        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY no está configurada en el backend.");
        }

        Map<String, Object> body = Map.of(
                "model", GROQ_MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", buildUserPrompt(request))
                ),
                "temperature", 0.4,
                "max_tokens", 512
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri(GROQ_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String answer = extractAnswer(response);
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("Groq no devolvió una respuesta válida.");
            }

            return new AiTutorResponse(answer.trim());
        } catch (RestClientResponseException exception) {
            logger.warn(
                    "Groq API error. model={}, status={}, body={}",
                    GROQ_MODEL,
                    exception.getStatusCode().value(),
                    sanitizeGroqErrorBody(exception.getResponseBodyAsString())
            );
            throw new IllegalStateException(GROQ_UNAVAILABLE_MESSAGE, exception);
        } catch (RestClientException exception) {
            logger.warn("Groq API request failed. model={}, message={}", GROQ_MODEL, exception.getMessage());
            throw new IllegalStateException(GROQ_UNAVAILABLE_MESSAGE, exception);
        }
    }

    private void validateRequest(AiTutorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El body de la solicitud es obligatorio.");
        }
        if (request.getStudentId() != null && request.getStudentId() <= 0) {
            throw new IllegalArgumentException("studentId debe ser un identificador válido.");
        }
        if (getEffectiveMessage(request).isBlank()) {
            throw new IllegalArgumentException("message o question es obligatorio.");
        }
    }

    private String buildUserPrompt(AiTutorRequest request) {
        String safeQuestion = limitText(getEffectiveMessage(request), 900);
        String safeContext = limitText(request.getContext(), 1200);
        String action = limitText(request.getAction(), 40).toUpperCase();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Pantalla actual: ").append(limitText(request.getCurrentScreen(), 60)).append("\n");
        appendIdentifier(prompt, "Módulo", request.getModuleId());
        appendIdentifier(prompt, "Nivel", request.getLevelId());
        appendIdentifier(prompt, "Lección", request.getLessonId());
        appendIdentifier(prompt, "Ejercicio", request.getExerciseId());
        if (!action.isBlank()) {
            prompt.append("Acción solicitada: ").append(action).append("\n");
        }
        if (!safeContext.isBlank()) {
            prompt.append("Contexto educativo del ejercicio: ").append(safeContext).append("\n");
        }
        prompt.append("Pregunta del estudiante: ").append(safeQuestion).append("\n");
        if ("HINT".equals(action)) {
            prompt.append("Regla obligatoria: ofrece solo una pista o el siguiente paso de razonamiento. No reveles la respuesta exacta, la opción correcta ni el resultado final.\n");
        }
        prompt.append("Responde como Neo con una explicación breve, paso a paso y enfocada en matemática.");
        return prompt.toString();
    }

    private String getEffectiveMessage(AiTutorRequest request) {
        if (request.getMessage() != null && !request.getMessage().isBlank()) {
            return request.getMessage().trim();
        }
        return request.getQuestion() == null ? "" : request.getQuestion().trim();
    }

    private void appendIdentifier(StringBuilder prompt, String label, Long value) {
        if (value != null) {
            prompt.append(label).append(" actual: ").append(value).append("\n");
        }
    }

    private String limitText(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }

        return normalized.substring(0, maxLength);
    }

    private String sanitizeGroqErrorBody(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        String sanitized = body
                .replaceAll("(?i)(bearer\\s+)[^\\s,\\\"]+", "$1[REDACTED]")
                .replaceAll("(?i)(api[_-]?key\\\"?\\s*[:=]\\s*\\\"?)[^\\s,\\\"]+", "$1[REDACTED]")
                .replaceAll("(?i)(authorization\\\"?\\s*[:=]\\s*\\\"?)[^\\s,\\\"]+", "$1[REDACTED]");

        int maxLength = 800;
        if (sanitized.length() <= maxLength) {
            return sanitized;
        }

        return sanitized.substring(0, maxLength) + "...";
    }

    private String extractAnswer(Map<?, ?> response) {
        if (response == null) {
            return null;
        }

        Object choicesValue = response.get("choices");
        if (!(choicesValue instanceof List<?> choices) || choices.isEmpty()) {
            return null;
        }

        Object firstChoiceValue = choices.get(0);
        if (!(firstChoiceValue instanceof Map<?, ?> firstChoice)) {
            return null;
        }

        Object messageValue = firstChoice.get("message");
        if (!(messageValue instanceof Map<?, ?> message)) {
            return null;
        }

        Object contentValue = message.get("content");
        if (contentValue instanceof String content && !content.isBlank()) {
            return content;
        }

        return null;
    }
}
