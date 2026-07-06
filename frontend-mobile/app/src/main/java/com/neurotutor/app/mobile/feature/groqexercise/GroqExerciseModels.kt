package com.neurotutor.app.mobile.feature.groqexercise

data class GroqExercise(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = -1,
    val hint: String = "",
    val successMessage: String = ""
) {
    val isValid: Boolean
        get() = question.isNotBlank() &&
            options.size in 2..4 &&
            options.all(String::isNotBlank) &&
            correctOptionIndex in options.indices
}

sealed interface GroqExerciseUiState {
    data object Idle : GroqExerciseUiState
    data object Loading : GroqExerciseUiState

    data class Content(
        val exercise: GroqExercise,
        val selectedOptionIndex: Int? = null
    ) : GroqExerciseUiState {
        val isCorrect: Boolean?
            get() = selectedOptionIndex?.let { it == exercise.correctOptionIndex }
    }

    data class Error(
        val message: String = SAFE_EXERCISE_ERROR
    ) : GroqExerciseUiState
}

internal const val SAFE_EXERCISE_ERROR =
    "Error al cargar el ejercicio. Reintentar."

