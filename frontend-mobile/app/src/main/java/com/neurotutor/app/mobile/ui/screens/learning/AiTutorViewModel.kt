package com.neurotutor.app.mobile.ui.screens.learning

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.AiTutorAction
import com.neurotutor.app.mobile.data.model.ai.AiTutorMessage
import com.neurotutor.app.mobile.data.model.ai.AiTutorMessageSender
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import com.neurotutor.app.mobile.data.repository.AiTutorPrompt
import com.neurotutor.app.mobile.data.repository.AiTutorRepository
import com.neurotutor.app.mobile.data.repository.AiTutorRepositoryContract
import com.neurotutor.app.mobile.data.repository.AiTutorRepositoryResult
import com.neurotutor.app.mobile.feature.groqexercise.GroqExerciseRepository
import com.neurotutor.app.mobile.feature.groqexercise.GroqExerciseRepositoryContract
import com.neurotutor.app.mobile.feature.groqexercise.GroqExerciseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiTutorViewModel(
    private val repository: AiTutorRepositoryContract = AiTutorRepository(),
    private val groqExerciseRepository: GroqExerciseRepositoryContract = GroqExerciseRepository()
) : ViewModel() {

    private val conversations = mutableMapOf<String, AiTutorUiState>()
    private val contexts = mutableMapOf<String, AiTutorConversationContext>()

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    fun selectConversation(context: AiTutorConversationContext) {
        val key = context.conversationKey
        
        // Estabilidad del contexto: No sobrescribir con datos vacíos si ya tenemos info rica
        val currentContext = contexts[key]
        val contextToSave = if (currentContext != null && context.exerciseQuestion.isBlank()) {
            currentContext
        } else {
            context
        }
        contexts[key] = contextToSave
        
        val existingState = conversations[key]
        val state = existingState ?: AiTutorUiState(
            messages = listOf(localGreeting(contextToSave)),
            conversationKey = key,
            entryPoint = contextToSave.entryPoint
        ).also { conversations[key] = it }
        _uiState.value = state
    }

    fun updateInput(text: String) {
        updateCurrentState { it.copy(inputText = text, errorMessage = null) }
    }

    fun sendMessage(
        message: String = _uiState.value.inputText,
        action: String? = null
    ) {
        val normalizedMessage = message.trim()
        val currentState = _uiState.value
        if (normalizedMessage.isEmpty() || currentState.isSending) return

        val conversationKey = currentState.conversationKey
        val context = contexts[conversationKey] ?: return

        // 1. Log en ViewModel al enviar
        Log.d("NEO_DEBUG", "ViewModel.sendMessage: action=$action, screen=${context.entryPoint.name}")
        Log.d("NEO_DEBUG", "Context: exerciseQuestion=${context.exerciseQuestion}, topicName=${context.topicName}")

        val userMessage = AiTutorMessage(
            sender = AiTutorMessageSender.STUDENT,
            contents = listOf(AiTutorContent.Text(normalizedMessage))
        )
        updateConversation(conversationKey) {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            if (action == "SIMILAR_EXERCISE") {
                handleSimilarExerciseAction(conversationKey, context)
            } else {
                performNormalAsk(normalizedMessage, action, conversationKey, context)
            }
        }
    }

    private suspend fun handleSimilarExerciseAction(
        conversationKey: String,
        context: AiTutorConversationContext
    ) {
        when (val result = groqExerciseRepository.loadExercise(
            studentId = context.studentId,
            moduleId = context.moduleId,
            topic = context.topicName
        )) {
            is GroqExerciseResult.Success -> {
                val exercise = result.exercise
                val neoMessage = AiTutorMessage(
                    sender = AiTutorMessageSender.NEO,
                    contents = listOf(
                        AiTutorContent.MultipleChoice(
                            InteractiveExercise(
                                id = exercise.id,
                                question = exercise.question,
                                options = exercise.options,
                                correctOptionIndex = exercise.correctOptionIndex,
                                hint = exercise.hint,
                                successMessage = exercise.successMessage
                            )
                        )
                    ),
                    suggestedActions = computeSuggestedActions(context)
                )
                updateConversation(conversationKey) {
                    it.copy(
                        messages = it.messages + neoMessage,
                        isSending = false,
                        errorMessage = null
                    )
                }
            }
            is GroqExerciseResult.Failure -> {
                updateConversation(conversationKey) {
                    it.copy(
                        isSending = false,
                        errorMessage = "Neo no pudo generar el ejercicio. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    private suspend fun performNormalAsk(
        message: String,
        action: String?,
        conversationKey: String,
        context: AiTutorConversationContext
    ) {
        when (
            val result = repository.askTutor(
                AiTutorPrompt(
                    studentId = context.studentId,
                    moduleId = context.moduleId,
                    question = message,
                    educationalContext = context.educationalContext(),
                    currentScreen = context.entryPoint.name,
                    action = action
                )
            )
        ) {
            is AiTutorRepositoryResult.Success -> {
                val neoMessage = AiTutorMessage(
                    sender = AiTutorMessageSender.NEO,
                    contents = result.contents,
                    suggestedActions = computeSuggestedActions(context)
                )
                updateConversation(conversationKey) {
                    it.copy(
                        messages = it.messages + neoMessage,
                        isSending = false,
                        errorMessage = null
                    )
                }
            }

            is AiTutorRepositoryResult.Error -> {
                updateConversation(conversationKey) {
                    it.copy(
                        isSending = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        updateCurrentState { it.copy(errorMessage = null) }
    }

    fun selectInteractiveOption(
        messageId: String,
        exerciseId: String,
        selectedOptionIndex: Int
    ) {
        val state = _uiState.value
        val stateKey = interactiveExerciseStateKey(messageId, exerciseId)
        if (state.interactiveExerciseStates.containsKey(stateKey)) return

        val exercise = state.messages
            .firstOrNull { it.id == messageId }
            ?.contents
            ?.filterIsInstance<AiTutorContent.MultipleChoice>()
            ?.firstOrNull { it.exercise.id == exerciseId }
            ?.exercise
            ?: return
        if (selectedOptionIndex !in exercise.options.indices) return

        val isCorrect = selectedOptionIndex == exercise.correctOptionIndex
        val feedback = if (isCorrect) {
            exercise.successMessage ?: "¡Muy bien! Tu razonamiento es correcto."
        } else {
            exercise.hint ?: "Observa nuevamente las opciones y prueba otra estrategia."
        }
        updateCurrentState {
            it.copy(
                interactiveExerciseStates = it.interactiveExerciseStates + (
                    stateKey to InteractiveExerciseUiState(
                        selectedOptionIndex = selectedOptionIndex,
                        isCorrect = isCorrect,
                        feedback = feedback
                    )
                )
            )
        }
    }

    fun onSuggestedAction(action: AiTutorAction) {
        if (_uiState.value.isSending) return
        when (action) {
            AiTutorAction.UNDERSTOOD -> addLocalStudentMessage("Ya entendí")
            AiTutorAction.HINT ->
                sendMessage(
                    message = "Dame una pista para continuar razonando",
                    action = "HINT"
                )
            AiTutorAction.SIMILAR_EXERCISE ->
                sendMessage(
                    message = "Quiero intentar un ejercicio parecido",
                    action = "SIMILAR_EXERCISE"
                )
            AiTutorAction.EXPLAIN_STEP_BY_STEP ->
                sendMessage(
                    message = "Explícame el procedimiento paso a paso",
                    action = "EXPLAIN_STEP_BY_STEP"
                )
        }
    }

    private fun updateCurrentState(transform: (AiTutorUiState) -> AiTutorUiState) {
        val key = _uiState.value.conversationKey
        if (key.isBlank()) return
        updateConversation(key, transform)
    }

    private fun updateConversation(
        key: String,
        transform: (AiTutorUiState) -> AiTutorUiState
    ) {
        val current = conversations[key] ?: return
        val updated = transform(current)
        conversations[key] = updated
        if (_uiState.value.conversationKey == key) {
            _uiState.value = updated
        }
    }

    private fun localGreeting(context: AiTutorConversationContext): AiTutorMessage {
        val greeting = when (context.entryPoint) {
            AiTutorEntryPoint.DASHBOARD ->
                "¡Hola${context.studentName.asGreetingName()}! ¿Qué te gustaría aprender hoy?"
            AiTutorEntryPoint.PRACTICE ->
                "Estoy aquí para ayudarte a pensar paso a paso sin darte la respuesta."
            AiTutorEntryPoint.THEORY ->
                "Puedo explicarte este concepto con un ejemplo sencillo."
            AiTutorEntryPoint.EXAM_REVIEW ->
                "Revisemos juntos el razonamiento paso a paso."
        }
        return AiTutorMessage(
            sender = AiTutorMessageSender.NEO,
            contents = listOf(AiTutorContent.Text(greeting)),
            suggestedActions = computeSuggestedActions(context, isGreeting = true)
        )
    }

    private fun String.asGreetingName(): String =
        trim().takeIf(String::isNotEmpty)?.let { ", $it" }.orEmpty()

    private fun addLocalStudentMessage(text: String) {
        val message = AiTutorMessage(
            sender = AiTutorMessageSender.STUDENT,
            contents = listOf(AiTutorContent.Text(text))
        )
        updateCurrentState {
            it.copy(messages = it.messages + message, errorMessage = null)
        }
    }

    private fun computeSuggestedActions(
        context: AiTutorConversationContext,
        isGreeting: Boolean = false
    ): List<AiTutorAction> {
        return when (context.entryPoint) {
            AiTutorEntryPoint.PRACTICE -> {
                // Habilitamos SIMILAR_EXERCISE siempre que tengamos el nombre del tema para Groq
                if (context.topicName.isNotBlank()) {
                    listOf(
                        AiTutorAction.UNDERSTOOD,
                        AiTutorAction.HINT,
                        AiTutorAction.SIMILAR_EXERCISE
                    )
                } else if (!context.exerciseId.isNullOrBlank()) {
                    listOf(
                        AiTutorAction.UNDERSTOOD,
                        AiTutorAction.HINT,
                        AiTutorAction.SIMILAR_EXERCISE
                    )
                } else {
                    listOf(AiTutorAction.UNDERSTOOD)
                }
            }
            AiTutorEntryPoint.DASHBOARD -> {
                // En el saludo del Dashboard no mostramos chips para mantener la limpieza.
                // En respuestas posteriores, solo "Entendí".
                if (isGreeting) emptyList() else listOf(AiTutorAction.UNDERSTOOD)
            }
            else -> listOf(AiTutorAction.UNDERSTOOD)
        }
    }
}
