package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.AiTutorRequest;
import com.neurotutor.user_service.dto.AiTutorResponse;
import com.neurotutor.user_service.dto.ProfileResponse;
import com.neurotutor.user_service.dto.StudentProgressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private static final String GROQ_MODEL = "llama-3.1-8b-instant";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_UNAVAILABLE_MESSAGE =
            "Neo IA está temporalmente no disponible. Intenta nuevamente en unos minutos.";

    private static final String SYSTEM_PROMPT = """
            Eres Neo, el tutor socrático de NeuroTutor para niños de primaria. Tu misión es guiar el descubrimiento matemático sin entregar jamás el resultado final.

            REGLAS DE IDENTIDAD Y TONO:
            - Eres un explorador matemático del futuro: entusiasta, paciente y 100% positivo.
            - Usa frases simples, cortas y directas. Evita el lenguaje artificial o excesivamente formal.
            - Máximo 50 palabras por respuesta para asegurar la legibilidad en dispositivos móviles.

            ESTRUCTURA DE RESPUESTA OBLIGATORIA:
            1. [Validación]: Reconoce el esfuerzo o el razonamiento del niño (ej: "¡Qué buena observación!").
            2. [Pista]: Una analogía o señal conceptual que aclare la duda sin resolver el ejercicio.
            3. [Pregunta Guía]: Una pregunta abierta que invite al niño a dar el siguiente paso lógico.

            ADAPTACIÓN POR NIVEL:
            - BÁSICO (🌱): Usa analogías físicas concretas (repartir dulces, rebanadas de pizza). No uses términos técnicos.
            - INTERMEDIO (🔥): Explica el "por qué" de los procedimientos. Introduce nombres de conceptos mezclados con ejemplos cotidianos.
            - AVANZADO (🚀): Usa terminología matemática precisa (ej: "mínimo común múltiplo", "proporcionalidad") pero acompáñala siempre de una explicación intuitiva para primaria.

            LÓGICA ANTE EL BLOQUEO:
            - Si detectas en el contexto que el niño ha fallado 4 o más veces en el mismo punto, rompe la restricción socrática: brinda una explicación explícita, paso a paso y muy desglosada del concepto, pero detente justo antes de revelar el resultado final.

            CONSISTENCIA Y SEGURIDAD:
            - Realiza una verificación interna de cualquier cálculo mencionado. Si no estás seguro de la exactitud matemática de una analogía, utiliza una más simple y probada.
            - Prohibido usar palabras negativas como "Mal", "Error" o "Equivocado".
            - Si preguntan algo ajeno a las matemáticas, redirige con amabilidad hacia la aventura del aprendizaje.
            """;

    private final RestClient restClient;
    private final ProfileService profileService;
    private final ProgressService progressService;

    public AiService(ProfileService profileService, ProgressService progressService) {
        this.restClient = RestClient.create();
        this.profileService = profileService;
        this.progressService = progressService;
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
                "max_tokens", 220
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

            return new AiTutorResponse(limitAnswerWords(answer.trim(), 120));
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

        String currentScreen = normalizeScreen(request.getCurrentScreen());
        Set<String> moduleScreens = Set.of(
                "THEORY",
                "PRACTICE",
                "LEVEL",
                "LEVEL_ACTIVITIES",
                "MODULE_DETAIL"
        );
        if (moduleScreens.contains(currentScreen) && request.getModuleId() == null) {
            throw new IllegalArgumentException(
                    "moduleId es obligatorio para la pantalla " + currentScreen + "."
            );
        }
    }

    private String buildUserPrompt(AiTutorRequest request) {
        String safeQuestion = limitText(getEffectiveMessage(request), 900);
        String safeContext = limitText(request.getContext(), 1200);
        String action = limitText(request.getAction(), 40).toUpperCase();

        StringBuilder prompt = new StringBuilder();
        String currentScreen = normalizeScreen(request.getCurrentScreen());
        prompt.append("Pantalla actual: ").append(currentScreen).append("\n");
        appendStudentContext(prompt, request, currentScreen);
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
        if ("SIMILAR_EXERCISE".equals(action)) {
            prompt.append("Crea un ejercicio parecido, pero no lo resuelvas por completo ni muestres la respuesta final.\n");
        }
        prompt.append("Responde como Neo en no más de 120 palabras, con frases cortas y apropiadas para sexto de primaria.");
        return prompt.toString();
    }

    private String limitAnswerWords(String answer, int maxWords) {
        String[] words = answer.split("\\s+");
        if (words.length <= maxWords) {
            return answer;
        }
        return String.join(" ", java.util.Arrays.copyOf(words, maxWords)) + "…";
    }

    private String getEffectiveMessage(AiTutorRequest request) {
        if (request.getMessage() != null && !request.getMessage().isBlank()) {
            return request.getMessage().trim();
        }
        return request.getQuestion() == null ? "" : request.getQuestion().trim();
    }

    private String normalizeScreen(String currentScreen) {
        return limitText(currentScreen, 60).toUpperCase(Locale.ROOT);
    }

    private void appendStudentContext(StringBuilder prompt, AiTutorRequest request, String currentScreen) {
        if (!("DASHBOARD".equals(currentScreen) || "LEARNING_PATH".equals(currentScreen))
                || request.getStudentId() == null) {
            return;
        }

        try {
            ProfileResponse profile = profileService.getProfile(request.getStudentId());
            StudentProgressResponse progress = progressService.getStudentProgress(request.getStudentId());
            prompt.append("Contexto real del estudiante:\n");
            prompt.append("- Nombre: ").append(profile.getName()).append("\n");
            prompt.append("- Grado: ").append(profile.getGrade()).append("\n");
            prompt.append("- Nivel diagnóstico: ").append(profile.getLevel()).append("\n");
            prompt.append("- Puntos: ").append(progress.getPoints()).append("\n");
            prompt.append("- Progreso general: ").append(progress.getOverallProgress()).append("%\n");
            prompt.append("- Módulos con actividad: ").append(progress.getModules().size()).append("\n");
        } catch (ProfileService.StudentProfileNotFoundException exception) {
            throw new IllegalArgumentException("No se encontró el estudiante indicado.");
        }
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
