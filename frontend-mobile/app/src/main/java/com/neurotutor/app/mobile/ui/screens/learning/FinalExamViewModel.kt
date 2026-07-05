package com.neurotutor.app.mobile.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.Exercise
import com.neurotutor.app.mobile.data.model.learning.FinalExamAnswerRequest
import com.neurotutor.app.mobile.data.model.learning.SubmitFinalExamAttemptRequest
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FinalExamUiState(
    val isLoading: Boolean = false,
    val questions: List<Exercise> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: List<Int?> = emptyList(),
    val isFinished: Boolean = false,
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val isPassed: Boolean = false,
    val pointsEarned: Int = 0,
    val levelUp: Boolean = false,
    val newLevel: String? = null,
    val topicCompleted: Boolean = false,
    val alreadyPassedBefore: Boolean = false,
    val moduleProgress: Int = 0,
    val unlockedBadgeId: String? = null,
    val unlockedAchievementCodes: List<String> = emptyList(),
    val showResultDialog: Boolean = false,
    val errorMessage: String? = null
)

class FinalExamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FinalExamUiState())
    val uiState: StateFlow<FinalExamUiState> = _uiState.asStateFlow()

    fun loadExam(moduleId: String, level: String) {
        // 🛡️ GUARDA: Evita recargas destructivas si ya hay preguntas en memoria
        if (_uiState.value.questions.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getFinalExam(moduleId)
                if (response.isSuccessful && response.body() != null) {
                    val examQuestions = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            questions = examQuestions,
                            answers = List(examQuestions.size) { null },
                            currentQuestionIndex = 0 // 🎯 Garantiza iniciar desde la primera pregunta
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cargar el examen.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun selectAnswer(index: Int) {
        _uiState.update { currentState ->
            val newAnswers = currentState.answers.toMutableList()
            newAnswers[currentState.currentQuestionIndex] = index
            currentState.copy(answers = newAnswers)
        }
    }

    fun nextQuestion() {
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        }
    }

    fun submitExam(studentId: String, moduleId: String, level: String) {
        val state = _uiState.value
        if (state.answers.any { it == null }) {
            _uiState.update {
                it.copy(errorMessage = "Responde todas las preguntas antes de enviar.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = SubmitFinalExamAttemptRequest(
                    studentId = studentId.replace("\"", "").trim().toLong(),
                    moduleId = moduleId.toLong(),
                    answers = state.questions.mapIndexed { index, question ->
                        FinalExamAnswerRequest(
                            questionId = question.id.toLong(),
                            selectedAnswerIndex = state.answers[index]!!
                        )
                    }
                )
                val response = RetrofitClient.apiService.submitFinalExamAttempt(request)
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isFinished = true,
                            score = result.scorePercentage,
                            correctAnswers = result.correctAnswers,
                            totalQuestions = result.totalQuestions,
                            isPassed = result.passed,
                            pointsEarned = result.pointsEarned,
                            alreadyPassedBefore = result.passed && result.unlockedBadgeId == null,
                            moduleProgress = result.moduleProgress,
                            unlockedBadgeId = result.unlockedBadgeId,
                            unlockedAchievementCodes = result.unlockedAchievementCodes,
                            showResultDialog = true,
                            errorMessage = null
                        )
                    }
                } else {
                    showSubmissionError(
                        "No se pudo guardar el resultado del examen. Inténtalo nuevamente."
                    )
                }
            } catch (e: Exception) {
                showSubmissionError(
                    "No se pudo conectar con el servidor. Verifica tu conexión e inténtalo nuevamente."
                )
            }
        }
    }

    fun dismissResultDialog() {
        _uiState.update { it.copy(showResultDialog = false) }
    }

    fun dismissSubmissionError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private suspend fun showSubmissionError(message: String) {
        withContext(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isFinished = false,
                    // ❌ SE ELIMINÓ EL RESET DE QUESTIONS Y ANSWERS PARA PERMITIR REINTENTOS SEGUROS
                    errorMessage = message
                )
            }
        }
    }
}
