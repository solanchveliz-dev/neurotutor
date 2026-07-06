package com.neurotutor.app.mobile.data.repository

import com.neurotutor.app.mobile.data.mapper.AiTutorActionContentNormalizer
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.model.common.AiTutorResponse
import com.neurotutor.app.mobile.ui.screens.learning.AiTutorConversationContext
import com.neurotutor.app.mobile.ui.screens.learning.AiTutorEntryPoint
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class AiTutorRepositoryTest {

    @Test
    fun networkFailureReturnsSafeError() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = { throw IOException("sensitive network detail") }
        )

        val result = repository.askTutor(prompt())

        assertTrue(result is AiTutorRepositoryResult.Error)
        val message = (result as AiTutorRepositoryResult.Error).message
        assertFalse(message.contains("sensitive"))
    }

    @Test
    fun malformedStructuredAnswerFallsBackToTextContent() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(AiTutorResponse("""{"contents":["""))
            }
        )

        val result = repository.askTutor(prompt())

        assertTrue(result is AiTutorRepositoryResult.Success)
        val content = (result as AiTutorRepositoryResult.Success).contents.single()
        assertTrue(content is AiTutorContent.Text)
        assertFalse((content as AiTutorContent.Text).text.contains("{"))
    }

    @Test
    fun practiceRequestNeverContainsOriginalCorrectAnswer() = runTest {
        var capturedRequest: AiTutorRequest? = null
        val repository = AiTutorRepository(
            askTutorCall = { request ->
                capturedRequest = request
                Response.success(AiTutorResponse("Observa las partes del ejercicio."))
            }
        )
        val context = AiTutorConversationContext(
            studentId = 7,
            entryPoint = AiTutorEntryPoint.PRACTICE,
            moduleId = 3,
            exerciseId = "exercise-12",
            moduleName = "Fracciones",
            topicName = "Comparación",
            exerciseQuestion = "¿Cuál fracción es mayor?",
            exerciseOptions = listOf("1/4", "3/4")
        )
        val originalCorrectAnswer = "ORIGINAL_CORRECT_ANSWER_MARKER"

        repository.askTutor(
            AiTutorPrompt(
                studentId = context.studentId,
                moduleId = context.moduleId,
                question = "Dame una pista",
                educationalContext = context.educationalContext(),
                currentScreen = "PRACTICE",
                action = "HINT"
            )
        )

        val requestText = listOf(
            capturedRequest?.question.orEmpty(),
            capturedRequest?.context.orEmpty()
        ).joinToString("\n")
        assertFalse(requestText.contains("correctAnswer", ignoreCase = true))
        assertFalse(requestText.contains("respuesta correcta", ignoreCase = true))
        assertFalse(requestText.contains(originalCorrectAnswer))
        assertEquals("PRACTICE", capturedRequest?.currentScreen)
        assertEquals("HINT", capturedRequest?.action)
    }

    @Test
    fun structuredContentIsPreferredWhenPresent() = runTest {
        val structuredContent =
            """{"contents":[{"type":"HINT_CARD","text":"Observa los denominadores."}]}"""
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(
                    AiTutorResponse(
                        answer = "Una pista breve para clientes antiguos.",
                        structuredContent = structuredContent
                    )
                )
            }
        )

        val result = repository.askTutor(prompt())

        assertTrue(result is AiTutorRepositoryResult.Success)
        val success = result as AiTutorRepositoryResult.Success
        assertEquals(
            listOf(AiTutorContent.HintCard("Observa los denominadores.")),
            success.contents
        )
        assertEquals("Una pista breve para clientes antiguos.", success.rawAnswer)
    }

    @Test
    fun missingStructuredContentFallsBackToAnswer() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(AiTutorResponse("Observa primero los datos."))
            }
        )

        val result = repository.askTutor(prompt()) as AiTutorRepositoryResult.Success

        assertEquals(
            listOf(AiTutorContent.Text("Observa primero los datos.")),
            result.contents
        )
    }

    @Test
    fun practiceSimilarExerciseKeepsBackendTextWhenStructuredContentIsMissing() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(
                    AiTutorResponse("Aquí tienes un ejercicio parecido explicado en texto.")
                )
            }
        )

        val result = repository.askTutor(
            prompt().copy(
                currentScreen = "PRACTICE",
                action = "SIMILAR_EXERCISE"
            )
        ) as AiTutorRepositoryResult.Success

        assertEquals(
            listOf(AiTutorContent.Text("Aquí tienes un ejercicio parecido explicado en texto.")),
            result.contents
        )
    }

    @Test
    fun similarExercisePrefersBackendMultipleChoiceOverText() {
        val multipleChoice = AiTutorContent.MultipleChoice(
            InteractiveExercise(
                id = "backend-similar-1",
                question = "¿Cuánto es 3 + 4?",
                options = listOf("6", "7", "8"),
                correctOptionIndex = 1,
                hint = "Suma tres y cuatro.",
                successMessage = "¡Correcto!"
            )
        )

        val contents = AiTutorActionContentNormalizer.normalize(
            action = "SIMILAR_EXERCISE",
            currentScreen = "PRACTICE",
            parsedContents = listOf(AiTutorContent.Text("Texto alternativo"), multipleChoice),
            educationalContext = "Tema: Sumas"
        )

        assertEquals(listOf(multipleChoice), contents)
    }

    @Test
    fun similarExerciseReturnsSafeTextWhenBackendContentIsEmpty() {
        val contents = AiTutorActionContentNormalizer.normalize(
            action = "SIMILAR_EXERCISE",
            currentScreen = "PRACTICE",
            parsedContents = emptyList(),
            educationalContext = "Tema: Fracciones"
        )

        assertEquals(
            listOf(
                AiTutorContent.Text(
                    "Neo no pudo mostrar el ejercicio parecido. Intentalo nuevamente."
                )
            ),
            contents
        )
    }

    @Test
    fun hintActionAlwaysReturnsHintCardWhenBackendReturnsText() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(AiTutorResponse("**Pista** Revisa el denominador."))
            }
        )

        val result = repository.askTutor(
            prompt().copy(currentScreen = "PRACTICE", action = "HINT")
        ) as AiTutorRepositoryResult.Success

        assertTrue(result.contents.single() is AiTutorContent.HintCard)
    }

    @Test
    fun stepActionKeepsBackendTextWhenStructuredContentIsMissing() = runTest {
        val repository = AiTutorRepository(
            askTutorCall = {
                Response.success(AiTutorResponse("Primero observa y luego calcula."))
            }
        )

        val result = repository.askTutor(
            prompt().copy(
                currentScreen = "PRACTICE",
                action = "EXPLAIN_STEP_BY_STEP"
            )
        ) as AiTutorRepositoryResult.Success

        assertEquals(
            listOf(AiTutorContent.Text("Primero observa y luego calcula.")),
            result.contents
        )
    }

    private fun prompt() = AiTutorPrompt(
        studentId = 1,
        moduleId = 2,
        question = "Ayúdame",
        educationalContext = "Tema: Fracciones"
    )
}
