package com.neurotutor.app.mobile.data.mapper

import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiTutorContentParserTest {

    @Test
    fun plainTextReturnsTextContentWithoutChanges() {
        val answer = "Observa primero qué partes tienen el mismo tamaño."

        assertEquals(
            listOf(AiTutorContent.Text(answer)),
            AiTutorContentParser.parse(answer)
        )
    }

    @Test
    fun validJsonParsesSupportedContents() {
        val result = AiTutorContentParser.parse(
            """
            {
              "contents": [
                {"type": "TEXT", "text": "Practiquemos con un ejemplo parecido."},
                {
                  "type": "STEP_EXPLANATION",
                  "title": "Pasos",
                  "steps": ["Observa los denominadores", "Compara las partes"]
                },
                {"type": "SOCRATIC_QUESTION", "question": "¿Qué notas en los denominadores?"},
                {
                  "type": "MULTIPLE_CHOICE",
                  "exercise": {
                    "id": "similar-1",
                    "question": "¿Cuál fracción es mayor?",
                    "options": ["1/4", "3/4", "2/4"],
                    "correctOptionIndex": 1,
                    "hint": "Compara cuántas partes se toman.",
                    "successMessage": "¡Muy bien razonado!"
                  }
                },
                {"type": "HINT_CARD", "text": "Dibuja las fracciones."},
                {"type": "SUCCESS_CARD", "text": "¡Ya dominaste esta idea!"}
              ]
            }
            """.trimIndent()
        )

        assertEquals(6, result.size)
        assertTrue(result[0] is AiTutorContent.Text)
        assertTrue(result[1] is AiTutorContent.StepExplanationContent)
        assertTrue(result[2] is AiTutorContent.SocraticQuestion)
        assertTrue(result[3] is AiTutorContent.MultipleChoice)
        assertTrue(result[4] is AiTutorContent.HintCard)
        assertTrue(result[5] is AiTutorContent.SuccessCard)
    }

    @Test
    fun malformedJsonUsesSafeTextFallback() {
        val result = AiTutorContentParser.parse("""{"contents": [""")
        val text = (result.single() as AiTutorContent.Text).text

        assertFalse(text.contains("{"))
        assertTrue(text.isNotBlank())
    }

    @Test
    fun unknownTypeIsIgnoredWhenAnotherBlockIsValid() {
        val result = AiTutorContentParser.parse(
            """
            {
              "contents": [
                {"type": "VIDEO", "url": "ignored"},
                {"type": "TEXT", "text": "Este bloque sí es válido."}
              ]
            }
            """.trimIndent()
        )

        assertEquals(
            listOf(AiTutorContent.Text("Este bloque sí es válido.")),
            result
        )
    }

    @Test
    fun missingRequiredFieldsUsesSafeTextFallback() {
        val result = AiTutorContentParser.parse(
            """{"contents":[{"type":"SOCRATIC_QUESTION"}]}"""
        )

        assertTrue(result.single() is AiTutorContent.Text)
    }

    @Test
    fun invalidCorrectOptionIndexUsesSafeTextFallback() {
        val result = AiTutorContentParser.parse(
            """
            {
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
            """.trimIndent()
        )

        assertTrue(result.single() is AiTutorContent.Text)
    }

    @Test
    fun validBlocksSurviveAlongsideInvalidBlocks() {
        val result = AiTutorContentParser.parse(
            """
            {
              "contents": [
                {"type": "TEXT"},
                {"type": "HINT_CARD", "text": "Busca partes del mismo tamaño."},
                {"type": "STEP_EXPLANATION", "steps": []}
              ]
            }
            """.trimIndent()
        )

        assertEquals(
            listOf(AiTutorContent.HintCard("Busca partes del mismo tamaño.")),
            result
        )
    }

    @Test
    fun accentsAndSpecialCharactersArePreserved() {
        val text = "¡Excelente! ¿Qué fracción representa ½ de la pizza? 🍕"
        val result = AiTutorContentParser.parse(
            """{"contents":[{"type":"TEXT","text":"$text"}]}"""
        )

        assertEquals(listOf(AiTutorContent.Text(text)), result)
    }

    @Test
    fun markdownTutorSectionsBecomeVisualContentWithoutRawMarkdown() {
        val result = AiTutorContentParser.parse(
            """
            **Validación**
            Notaste que los denominadores son diferentes.

            **Pista**
            Busca primero un denominador común.

            **Pregunta guía**
            ¿Qué número es múltiplo de 2 y de 3?
            """.trimIndent()
        )

        assertEquals(3, result.size)
        assertTrue(result[0] is AiTutorContent.ValidationCard)
        assertTrue(result[1] is AiTutorContent.HintCard)
        assertTrue(result[2] is AiTutorContent.SocraticQuestion)
        assertFalse(result.joinToString().contains("**"))
    }

    @Test
    fun markdownDecorationIsRemovedFromPlainTextFallback() {
        val result = AiTutorContentParser.parse(
            "Observa **con cuidado** los denominadores."
        )

        assertEquals(
            listOf(AiTutorContent.Text("Observa con cuidado los denominadores.")),
            result
        )
    }

    @Test
    fun completeSimilarExerciseBecomesInteractiveMultipleChoice() {
        val result = AiTutorContentParser.parse(
            """
            **Ejercicio parecido**
            ¿Cuál fracción representa tres de cuatro partes?
            A) 1/4
            B) 2/4
            C) 3/4
            Respuesta correcta: C
            Pista: Cuenta cuántas partes se tomaron.
            """.trimIndent()
        )

        val exercise = result.filterIsInstance<AiTutorContent.MultipleChoice>().single().exercise
        assertEquals(listOf("1/4", "2/4", "3/4"), exercise.options)
        assertEquals(2, exercise.correctOptionIndex)
        assertEquals("Cuenta cuántas partes se tomaron.", exercise.hint)
        assertFalse(exercise.question.contains("Respuesta correcta"))
    }

    @Test
    fun incompleteSimilarExerciseFallsBackToCleanText() {
        val result = AiTutorContentParser.parse(
            """
            **Ejercicio parecido**
            Piensa en una fracción parecida y dibújala.
            """.trimIndent()
        )

        val text = (result.single() as AiTutorContent.Text).text
        assertFalse(text.contains("**"))
        assertTrue(text.contains("Piensa en una fracción"))
    }
}
