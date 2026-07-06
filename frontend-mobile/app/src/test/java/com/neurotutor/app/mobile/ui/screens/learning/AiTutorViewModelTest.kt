package com.neurotutor.app.mobile.ui.screens.learning

import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.AiTutorAction
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import com.neurotutor.app.mobile.data.repository.AiTutorRepositoryContract
import com.neurotutor.app.mobile.data.repository.AiTutorRepositoryResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiTutorViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun secondSendIsIgnoredWhileRequestIsActive() = runTest(dispatcher) {
        var callCount = 0
        val responseGate = CompletableDeferred<AiTutorRepositoryResult>()
        val repository = AiTutorRepositoryContract {
            callCount++
            responseGate.await()
        }
        val viewModel = AiTutorViewModel(repository)
        viewModel.selectConversation(context(AiTutorEntryPoint.PRACTICE, "exercise-1"))

        viewModel.sendMessage("Necesito una pista")
        viewModel.sendMessage("Necesito otra pista")
        runCurrent()

        assertEquals(1, callCount)
        assertTrue(viewModel.uiState.value.isSending)

        responseGate.complete(success("Observa el denominador."))
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSending)
    }

    @Test
    fun historiesRemainSeparatedByConversationKey() = runTest(dispatcher) {
        val repository = AiTutorRepositoryContract {
            success("Respuesta para esta conversación")
        }
        val viewModel = AiTutorViewModel(repository)
        val dashboard = context(AiTutorEntryPoint.DASHBOARD)
        val practice = context(AiTutorEntryPoint.PRACTICE, "exercise-2")

        viewModel.selectConversation(dashboard)
        viewModel.sendMessage("¿Qué estudio hoy?")
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.messages.size)

        viewModel.selectConversation(practice)
        assertEquals(1, viewModel.uiState.value.messages.size)
        assertEquals(practice.conversationKey, viewModel.uiState.value.conversationKey)

        viewModel.selectConversation(dashboard)
        assertEquals(3, viewModel.uiState.value.messages.size)
        assertEquals(dashboard.conversationKey, viewModel.uiState.value.conversationKey)
    }

    @Test
    fun repositoryErrorIsExposedWithoutAddingNeoMessage() = runTest(dispatcher) {
        val repository = AiTutorRepositoryContract {
            AiTutorRepositoryResult.Error("No pudimos conectar con Neo.")
        }
        val viewModel = AiTutorViewModel(repository)
        viewModel.selectConversation(context(AiTutorEntryPoint.THEORY))

        viewModel.sendMessage("Explícame este concepto")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.messages.size)
        assertEquals("No pudimos conectar con Neo.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSending)
    }

    @Test
    fun correctInteractiveOptionStoresSuccessState() = runTest(dispatcher) {
        val viewModel = viewModelWithInteractiveExercise()
        val neoMessage = viewModel.uiState.value.messages.last()
        val exercise = neoMessage.contents
            .filterIsInstance<AiTutorContent.MultipleChoice>()
            .single()
            .exercise

        viewModel.selectInteractiveOption(neoMessage.id, exercise.id, 1)

        val state = viewModel.uiState.value.interactiveExerciseStates[
            interactiveExerciseStateKey(neoMessage.id, exercise.id)
        ]
        assertTrue(state?.isCorrect == true)
        assertEquals("¡Excelente razonamiento!", state?.feedback)
    }

    @Test
    fun incorrectInteractiveOptionStoresHintState() = runTest(dispatcher) {
        val viewModel = viewModelWithInteractiveExercise()
        val neoMessage = viewModel.uiState.value.messages.last()
        val exercise = neoMessage.contents
            .filterIsInstance<AiTutorContent.MultipleChoice>()
            .single()
            .exercise

        viewModel.selectInteractiveOption(neoMessage.id, exercise.id, 0)

        val state = viewModel.uiState.value.interactiveExerciseStates[
            interactiveExerciseStateKey(neoMessage.id, exercise.id)
        ]
        assertTrue(state?.isCorrect == false)
        assertEquals("Compara cuántas partes se toman.", state?.feedback)
    }

    @Test
    fun suggestedActionDoesNotDuplicateRequestWhileSending() = runTest(dispatcher) {
        var callCount = 0
        var capturedPrompt: com.neurotutor.app.mobile.data.repository.AiTutorPrompt? = null
        val responseGate = CompletableDeferred<AiTutorRepositoryResult>()
        val repository = AiTutorRepositoryContract { prompt ->
            callCount++
            capturedPrompt = prompt
            responseGate.await()
        }
        val viewModel = AiTutorViewModel(repository)
        viewModel.selectConversation(context(AiTutorEntryPoint.PRACTICE, "exercise-3"))

        viewModel.onSuggestedAction(AiTutorAction.HINT)
        viewModel.onSuggestedAction(AiTutorAction.HINT)
        runCurrent()

        assertEquals(1, callCount)
        assertEquals("HINT", capturedPrompt?.action)
        assertEquals("PRACTICE", capturedPrompt?.currentScreen)
        responseGate.complete(success("Observa las partes."))
        advanceUntilIdle()
    }

    private fun context(
        entryPoint: AiTutorEntryPoint,
        exerciseId: String? = null
    ) = AiTutorConversationContext(
        studentId = 10,
        entryPoint = entryPoint,
        moduleId = 20,
        exerciseId = exerciseId,
        studentName = "Ana",
        moduleName = "Fracciones"
    )

    private fun success(text: String) = AiTutorRepositoryResult.Success(
        contents = listOf(AiTutorContent.Text(text)),
        rawAnswer = text
    )

    private suspend fun TestScope.viewModelWithInteractiveExercise(): AiTutorViewModel {
        val repository = AiTutorRepositoryContract {
            AiTutorRepositoryResult.Success(
                contents = listOf(
                    AiTutorContent.MultipleChoice(
                        InteractiveExercise(
                            id = "similar-1",
                            question = "¿Cuál fracción es mayor?",
                            options = listOf("1/4", "3/4"),
                            correctOptionIndex = 1,
                            hint = "Compara cuántas partes se toman.",
                            successMessage = "¡Excelente razonamiento!"
                        )
                    )
                ),
                rawAnswer = "structured"
            )
        }
        return AiTutorViewModel(repository).also {
            it.selectConversation(context(AiTutorEntryPoint.PRACTICE, "exercise-4"))
            it.sendMessage("Quiero practicar")
            advanceUntilIdle()
        }
    }
}
