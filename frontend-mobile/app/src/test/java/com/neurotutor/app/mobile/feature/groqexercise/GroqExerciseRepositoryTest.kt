package com.neurotutor.app.mobile.feature.groqexercise

import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.model.common.AiTutorResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class GroqExerciseRepositoryTest {

    @Test
    fun validStructuredExerciseReturnsSuccessAndUsesCompatibleRequest() = runTest {
        var capturedRequest: AiTutorRequest? = null
        val repository = GroqExerciseRepository { request ->
            capturedRequest = request
            Response.success(
                AiTutorResponse(
                    answer = "Ejercicio preparado",
                    structuredContent = validContentsJson()
                )
            )
        }

        val result = repository.loadExercise(1, 2, "sumas")

        assertEquals(GroqExercisePrompt.ACTION, capturedRequest?.action)
        assertEquals(GroqExercisePrompt.SCREEN, capturedRequest?.currentScreen)
        assertEquals(1L, capturedRequest?.studentId)
        assertEquals(2L, capturedRequest?.moduleId)
        assertTrue(result is GroqExerciseResult.Success)
        assertEquals(
            "¿Cuánto es 5 + 3?",
            (result as GroqExerciseResult.Success).exercise.question
        )
    }

    @Test
    fun legacyAnswerWithMarkdownFencedWrapperReturnsSuccess() = runTest {
        val legacyAnswer = """
            ```json
            {
              "answer": "Ejercicio preparado",
              "structuredContent": ${validContentsJson()}
            }
            ```
        """.trimIndent()
        val repository = GroqExerciseRepository {
            Response.success(AiTutorResponse(answer = legacyAnswer))
        }

        val result = repository.loadExercise(1, 2, "fracciones")

        assertTrue(result is GroqExerciseResult.Success)
    }

    @Test
    fun directLegacyContentsInAnswerReturnsSuccess() = runTest {
        val repository = GroqExerciseRepository {
            Response.success(AiTutorResponse(answer = validContentsJson()))
        }

        val result = repository.loadExercise(1, 2, "fracciones")

        assertTrue(result is GroqExerciseResult.Success)
    }

    @Test
    fun textOnlyResponseReturnsSafeFailure() = runTest {
        val repository = GroqExerciseRepository {
            Response.success(AiTutorResponse(answer = "Respuesta sin JSON válido"))
        }

        val result = repository.loadExercise(1, 2, "fracciones")

        assertEquals(GroqExerciseResult.Failure(), result)
    }

    @Test
    fun invalidCorrectOptionIndexReturnsSafeFailure() = runTest {
        val invalidJson = validContentsJson().replace(
            "\"correctOptionIndex\": 1",
            "\"correctOptionIndex\": 9"
        )
        val repository = GroqExerciseRepository {
            Response.success(
                AiTutorResponse(
                    answer = "Ejercicio preparado",
                    structuredContent = invalidJson
                )
            )
        }

        val result = repository.loadExercise(1, 2, "fracciones")

        assertEquals(GroqExerciseResult.Failure(), result)
    }

    @Test
    fun unexpectedExceptionReturnsSafeFailure() = runTest {
        val repository = GroqExerciseRepository {
            throw IllegalStateException("detalle sensible")
        }

        val result = repository.loadExercise(1, 2, "fracciones")

        assertEquals(GroqExerciseResult.Failure(), result)
    }

    private fun validContentsJson(): String = """
        {
          "contents": [{
            "type": "MULTIPLE_CHOICE",
            "exercise": {
              "id": "groq-1",
              "question": "¿Cuánto es 5 + 3?",
              "options": ["7", "8", "9"],
              "correctOptionIndex": 1,
              "hint": "Cuenta tres pasos desde cinco.",
              "successMessage": "¡Correcto!"
            }
          }]
        }
    """.trimIndent()
}
