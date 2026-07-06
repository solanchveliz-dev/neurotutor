package com.neurotutor.app.mobile.ui.screens.learning

import com.neurotutor.app.mobile.data.model.ai.AiTutorMessage

enum class AiTutorEntryPoint {
    DASHBOARD,
    PRACTICE,
    THEORY,
    EXAM_REVIEW
}

data class AiTutorConversationContext(
    val studentId: Long,
    val entryPoint: AiTutorEntryPoint,
    val moduleId: Long,
    val exerciseId: String? = null,
    val reviewId: String? = null,
    val studentName: String = "",
    val moduleName: String = "",
    val topicName: String = "",
    val exerciseQuestion: String = "",
    val exerciseOptions: List<String> = emptyList()
) {
    val conversationKey: String
        get() {
            val contextId = when (entryPoint) {
                AiTutorEntryPoint.PRACTICE -> exerciseId.orEmpty().ifBlank { "general" }
                AiTutorEntryPoint.EXAM_REVIEW -> reviewId.orEmpty().ifBlank { "general" }
                AiTutorEntryPoint.DASHBOARD,
                AiTutorEntryPoint.THEORY -> "general"
            }
            return "$studentId:${entryPoint.name}:$moduleId:$contextId"
        }

    fun educationalContext(): String = buildList {
        add("Punto de entrada: ${entryPoint.name}")
        moduleName.takeIf(String::isNotBlank)?.let { add("Módulo: $it") }
        topicName.takeIf(String::isNotBlank)?.let { add("Tema: $it") }
        if (entryPoint == AiTutorEntryPoint.PRACTICE) {
            exerciseQuestion.takeIf(String::isNotBlank)?.let {
                add("Ejercicio actual: $it")
            }
            exerciseOptions.takeIf(List<String>::isNotEmpty)?.let {
                add("Opciones del ejercicio: ${it.joinToString(" | ")}")
            }
            add(
                "Regla pedagógica: guía el razonamiento sin revelar la solución " +
                    "del ejercicio actual."
            )
        }
        if (entryPoint == AiTutorEntryPoint.EXAM_REVIEW) {
            add("La evaluación ya terminó; puedes explicar el razonamiento paso a paso.")
        }
    }.joinToString(separator = "\n")
}

data class AiTutorUiState(
    val messages: List<AiTutorMessage> = emptyList(),
    val interactiveExerciseStates: Map<String, InteractiveExerciseUiState> = emptyMap(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val conversationKey: String = "",
    val entryPoint: AiTutorEntryPoint = AiTutorEntryPoint.DASHBOARD
)

data class InteractiveExerciseUiState(
    val selectedOptionIndex: Int,
    val isCorrect: Boolean,
    val feedback: String
)

fun interactiveExerciseStateKey(messageId: String, exerciseId: String): String =
    "$messageId:$exerciseId"
