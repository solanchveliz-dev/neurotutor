package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurotutor.user_service.dto.AiTutorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiTutorResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void legacyResponseKeepsAnswerAndOmitsOptionalStructuredContent() throws Exception {
        JsonNode json = objectMapper.readTree(
                objectMapper.writeValueAsString(new AiTutorResponse("Respuesta para React"))
        );

        assertEquals("Respuesta para React", json.get("answer").asText());
        assertFalse(json.has("structuredContent"));
    }

    @Test
    void structuredResponseKeepsLegacyAnswer() throws Exception {
        String structuredContent =
                "{\"contents\":[{\"type\":\"HINT_CARD\",\"text\":\"Observa los datos.\"}]}";
        JsonNode json = objectMapper.readTree(
                objectMapper.writeValueAsString(
                        new AiTutorResponse("Una pista breve.", structuredContent)
                )
        );

        assertEquals("Una pista breve.", json.get("answer").asText());
        assertEquals(structuredContent, json.get("structuredContent").asText());
        assertTrue(json.has("structuredContent"));
    }
}
