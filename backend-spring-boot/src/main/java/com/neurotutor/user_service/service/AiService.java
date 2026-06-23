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
    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";
    private static final String GEMINI_UNAVAILABLE_MESSAGE =
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
            """;

    private final RestClient restClient;

    public AiService() {
        this.restClient = RestClient.create();
    }

    public AiTutorResponse askTutor(AiTutorRequest request) {
        validateRequest(request);

        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY no está configurada en el backend.");
        }

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                ),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildUserPrompt(request)))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 512
                )
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri(GEMINI_URL + "?key={apiKey}", apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String answer = extractAnswer(response);
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("Gemini no devolvió una respuesta válida.");
            }

            return new AiTutorResponse(answer.trim());
        } catch (RestClientResponseException exception) {
            logger.warn(
                    "Gemini API error. model={}, status={}, body={}",
                    GEMINI_MODEL,
                    exception.getStatusCode().value(),
                    sanitizeGeminiErrorBody(exception.getResponseBodyAsString())
            );
            throw new IllegalStateException(GEMINI_UNAVAILABLE_MESSAGE, exception);
        } catch (RestClientException exception) {
            logger.warn("Gemini API request failed. model={}, message={}", GEMINI_MODEL, exception.getMessage());
            throw new IllegalStateException(GEMINI_UNAVAILABLE_MESSAGE, exception);
        }
    }

    private void validateRequest(AiTutorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El body de la solicitud es obligatorio.");
        }
        if (request.getStudentId() == null) {
            throw new IllegalArgumentException("studentId es obligatorio.");
        }
        if (request.getModuleId() == null) {
            throw new IllegalArgumentException("moduleId es obligatorio.");
        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new IllegalArgumentException("question es obligatoria.");
        }
    }

    private String buildUserPrompt(AiTutorRequest request) {
        String safeQuestion = limitText(request.getQuestion(), 900);
        String safeContext = limitText(request.getContext(), 1200);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Módulo actual: ").append(request.getModuleId()).append("\n");
        if (!safeContext.isBlank()) {
            prompt.append("Contexto educativo del ejercicio: ").append(safeContext).append("\n");
        }
        prompt.append("Pregunta del estudiante: ").append(safeQuestion).append("\n");
        prompt.append("Responde como Neo con una explicación breve, paso a paso y enfocada en matemática.");
        return prompt.toString();
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

    private String sanitizeGeminiErrorBody(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        String sanitized = body
                .replaceAll("(?i)(key=)[^\\s&\\\"]+", "$1[REDACTED]")
                .replaceAll("(?i)(api[_-]?key\\\"?\\s*[:=]\\s*\\\"?)[^\\s,\\\"]+", "$1[REDACTED]");

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

        Object candidatesValue = response.get("candidates");
        if (!(candidatesValue instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }

        Object firstCandidateValue = candidates.get(0);
        if (!(firstCandidateValue instanceof Map<?, ?> firstCandidate)) {
            return null;
        }

        Object contentValue = firstCandidate.get("content");
        if (!(contentValue instanceof Map<?, ?> content)) {
            return null;
        }

        Object partsValue = content.get("parts");
        if (!(partsValue instanceof List<?> parts) || parts.isEmpty()) {
            return null;
        }

        StringBuilder answer = new StringBuilder();
        for (Object partValue : parts) {
            if (partValue instanceof Map<?, ?> part) {
                Object textValue = part.get("text");
                if (textValue instanceof String text && !text.isBlank()) {
                    if (!answer.isEmpty()) {
                        answer.append("\n");
                    }
                    answer.append(text);
                }
            }
        }

        return answer.toString();
    }
}
