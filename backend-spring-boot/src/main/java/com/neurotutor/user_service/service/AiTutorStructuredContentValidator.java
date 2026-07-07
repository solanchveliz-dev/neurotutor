package com.neurotutor.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.Set;

public final class AiTutorStructuredContentValidator {
    private static final Set<String> HINT_ACTIONS = Set.of("HINT", "REQUEST_HINT");
    private static final Set<String> STEP_ACTIONS = Set.of("PROCEDURE", "EXPLAIN_STEP_BY_STEP");
    private static final Set<String> SIMILAR_EXERCISE_ACTIONS =
            Set.of("SIMILAR_EXERCISE", "TRY_SIMILAR_EXERCISE");

    private final ObjectMapper objectMapper;

    public AiTutorStructuredContentValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<ValidatedStructuredResponse> validate(String rawResponse, String action) {
        try {
            JsonNode root = objectMapper.readTree(removeCodeFence(rawResponse));
            String answer = requiredText(root, "answer");
            JsonNode structuredContent = root.get("structuredContent");
            JsonNode contents = structuredContent == null ? null : structuredContent.get("contents");
            if (answer == null || structuredContent == null || !structuredContent.isObject()
                    || contents == null || !contents.isArray() || contents.size() == 0) {
                return Optional.empty();
            }

            String normalizedAction = action == null ? "" : action.trim().toUpperCase();
            boolean valid = contents.size() == 1
                    && validateContent(contents.get(0), normalizedAction);
            if (!valid) {
                return Optional.empty();
            }

            return Optional.of(
                    new ValidatedStructuredResponse(
                            answer,
                            objectMapper.writeValueAsString(structuredContent)
                    )
            );
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public static boolean supports(String action) {
        if (action == null) {
            return false;
        }
        String normalized = action.trim().toUpperCase();
        return HINT_ACTIONS.contains(normalized)
                || STEP_ACTIONS.contains(normalized)
                || SIMILAR_EXERCISE_ACTIONS.contains(normalized);
    }

    private boolean validateContent(JsonNode content, String action) {
        if (content == null || !content.isObject()) {
            return false;
        }
        String type = requiredText(content, "type");
        if (type == null) {
            return false;
        }
        if (HINT_ACTIONS.contains(action)) {
            return "HINT_CARD".equals(type) && requiredText(content, "text") != null;
        }
        if (STEP_ACTIONS.contains(action)) {
            return validateStepExplanation(content, type);
        }
        if (SIMILAR_EXERCISE_ACTIONS.contains(action)) {
            return validateMultipleChoice(content, type);
        }
        return false;
    }

    private boolean validateStepExplanation(JsonNode content, String type) {
        if (!"STEP_EXPLANATION".equals(type)) {
            return false;
        }
        JsonNode steps = content.get("steps");
        if (steps == null || !steps.isArray() || steps.size() == 0 || steps.size() > 5) {
            return false;
        }
        for (JsonNode step : steps) {
            if (!step.isTextual() || step.asText().isBlank()) {
                return false;
            }
        }
        return true;
    }

    private boolean validateMultipleChoice(JsonNode content, String type) {
        if (!"MULTIPLE_CHOICE".equals(type)) {
            return false;
        }
        JsonNode exercise = content.get("exercise");
        if (exercise == null || !exercise.isObject()
                || requiredText(exercise, "id") == null
                || requiredText(exercise, "question") == null) {
            return false;
        }
        JsonNode options = exercise.get("options");
        JsonNode correctOptionIndex = exercise.get("correctOptionIndex");
        if (options == null || !options.isArray() || options.size() < 2 || options.size() > 4
                || correctOptionIndex == null || !correctOptionIndex.canConvertToInt()) {
            return false;
        }
        for (JsonNode option : options) {
            if (!option.isTextual() || option.asText().isBlank()) {
                return false;
            }
        }
        int index = correctOptionIndex.asInt(-1);
        return index >= 0 && index < options.size();
    }

    private String requiredText(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            return null;
        }
        return value.asText().trim();
    }

    private String removeCodeFence(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }
        return trimmed
                .replaceFirst("^```(?:json)?\\s*", "")
                .replaceFirst("\\s*```$", "")
                .trim();
    }

    public record ValidatedStructuredResponse(String answer, String structuredContent) {}
}
