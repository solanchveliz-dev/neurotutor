package com.neurotutor.app.mobile.feature.groqexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroqExerciseViewModel(
    private val repository: GroqExerciseRepositoryContract = GroqExerciseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroqExerciseUiState>(GroqExerciseUiState.Idle)
    val uiState: StateFlow<GroqExerciseUiState> = _uiState.asStateFlow()

    private var lastRequest: ExerciseRequest? = null

    fun loadExercise(studentId: Long, moduleId: Long, topic: String) {
        if (_uiState.value is GroqExerciseUiState.Loading) return

        val request = ExerciseRequest(studentId, moduleId, topic)
        lastRequest = request
        _uiState.value = GroqExerciseUiState.Loading

        viewModelScope.launch {
            _uiState.value = when (
                val result = repository.loadExercise(
                    studentId = request.studentId,
                    moduleId = request.moduleId,
                    topic = request.topic
                )
            ) {
                is GroqExerciseResult.Success -> GroqExerciseUiState.Content(result.exercise)
                is GroqExerciseResult.Failure -> GroqExerciseUiState.Error(result.message)
            }
        }
    }

    fun retry() {
        val request = lastRequest ?: return
        loadExercise(request.studentId, request.moduleId, request.topic)
    }

    fun selectOption(index: Int) {
        val current = _uiState.value as? GroqExerciseUiState.Content ?: return
        if (current.selectedOptionIndex != null || index !in current.exercise.options.indices) return
        _uiState.value = current.copy(selectedOptionIndex = index)
    }

    private data class ExerciseRequest(
        val studentId: Long,
        val moduleId: Long,
        val topic: String
    )
}

