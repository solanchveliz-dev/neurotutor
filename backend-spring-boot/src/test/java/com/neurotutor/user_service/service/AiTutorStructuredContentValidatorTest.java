package com.neurotutor.user_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurotutor.user_service.service.AiTutorStructuredContentValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiTutorStructuredContentValidatorTest {
    private final AiTutorStructuredContentValidator validator =
            new AiTutorStructuredContentValidator(new ObjectMapper());

    @Test
    void acceptsHintForNewAndLegacyActionNames() {
        String response = """
                {
                  "answer": "Observa los datos importantes.",
                  "structuredContent": {
                    "contents": [{
                      "type": "HINT_CARD",
                      "text": "Busca primero qué cantidades puedes comparar."
                    }]
                  }
                }
                """;

        assertTrue(validator.validate(response, "REQUEST_HINT").isPresent());
        assertTrue(validator.validate(response, "HINT").isPresent());
    }

    @Test
    void acceptsStepExplanationWithoutFinalAnswer() {
        String response = """
                {
                  "answer": "Vamos a dividirlo en pasos.",
                  "structuredContent": {
                    "contents": [{
                      "type": "STEP_EXPLANATION",
                      "title": "Pasos para pensar",
                      "steps": ["Observa los datos", "Elige la operación"]
                    }]
                  }
                }
                """;

        assertTrue(validator.validate(response, "EXPLAIN_STEP_BY_STEP").isPresent());
        assertTrue(validator.validate(response, "PROCEDURE").isPresent());
    }

    @Test
    void acceptsSimilarExerciseAndKeepsCorrectIndexInsideStructuredContent() {
        String response = """
                {
                  "answer": "Te preparé un ejercicio parecido.",
                  "structuredContent": {
                    "contents": [{
                      "type": "MULTIPLE_CHOICE",
                      "exercise": {
                        "id": "similar-1",
                        "question": "¿Cuál fracción representa tres de cuatro partes?",
                        "options": ["1/4", "2/4", "3/4"],
                        "correctOptionIndex": 2,
                        "hint": "Cuenta las partes seleccionadas."
                      }
                    }]
                  }
                }
                """;

        var result = validator.validate(response, "TRY_SIMILAR_EXERCISE");

        assertTrue(result.isPresent());
        assertEquals("Te preparé un ejercicio parecido.", result.get().answer());
        assertFalse(result.get().answer().contains("2"));
        assertTrue(result.get().structuredContent().contains("\"correctOptionIndex\":2"));
    }

    @Test
    void rejectsInvalidCorrectOptionIndex() {
        String response = """
                {
                  "answer": "Practiquemos.",
                  "structuredContent": {
                    "contents": [{
                      "type": "MULTIPLE_CHOICE",
                      "exercise": {
                        "id": "similar-2",
                        "question": "¿Cuál opción corresponde?",
                        "options": ["A", "B"],
                        "correctOptionIndex": 4
                      }
                    }]
                  }
                }
                """;

        assertTrue(validator.validate(response, "SIMILAR_EXERCISE").isEmpty());
    }

    @Test
    void rejectsMalformedJson() {
        assertTrue(
                validator.validate("{\"answer\":", "REQUEST_HINT").isEmpty()
        );
    }
}
