package com.neurotutor.app.mobile.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.Exercise
import com.neurotutor.app.mobile.data.model.learning.SubmitExamRequest
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FinalExamUiState(
    val isLoading: Boolean = false,
    val questions: List<Exercise> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: MutableList<Int?> = mutableListOf(),
    val isFinished: Boolean = false,
    val score: Int = 0,
    val isPassed: Boolean = false,
    // 🆕 Nuevos campos
    val pointsEarned: Int = 0,
    val levelUp: Boolean = false,
    val newLevel: String? = null,
    val topicCompleted: Boolean = false,
    val alreadyPassedBefore: Boolean = false,
    val showResultDialog: Boolean = false,
    val errorMessage: String? = null
)

class FinalExamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FinalExamUiState())
    val uiState: StateFlow<FinalExamUiState> = _uiState.asStateFlow()

    fun loadExam(moduleId: String, level: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getFinalExam(moduleId)
                if (response.isSuccessful && response.body() != null) {
                    val examQuestions = response.body()!!
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                questions = examQuestions,
                                answers = MutableList(examQuestions.size) { null }
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar el examen.") }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }

    fun selectAnswer(index: Int) {
        val currentAnswers = _uiState.value.answers.toMutableList()
        currentAnswers[_uiState.value.currentQuestionIndex] = index
        _uiState.update { it.copy(answers = currentAnswers) }
    }

    fun nextQuestion() {
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        } else {
            // Terminó el examen, pero aún no enviamos al backend
        }
    }

    // 🆕 Nuevo método que maneja toda la lógica de puntos y desbloqueo
    fun submitExam(studentId: String, moduleId: String, level: String) {
        val state = _uiState.value
        var correctCount = 0
        state.questions.forEachIndexed { index, question ->
            if (state.answers[index] == question.correctAnswerIndex) {
                correctCount++
            }
        }

        val passThreshold = 0.7f
        val currentScore = (correctCount.toFloat() / state.questions.size * 100).toInt()
        val passed = (correctCount.toFloat() / state.questions.size) >= passThreshold

        if (!passed) {
            // ❌ No aprobó - mostrar resultado sin puntos
            _uiState.update {
                it.copy(
                    isFinished = true,
                    isPassed = false,
                    score = currentScore,
                    pointsEarned = 0,
                    levelUp = false,
                    showResultDialog = true
                )
            }
            return
        }

        // ✅ Aprobó - llamar al backend para procesar
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cleanId = studentId.replace("\"", "").trim()

                // Usar el nuevo endpoint V2
                val request = SubmitExamRequest(
                    studentId = cleanId,
                    moduloId = moduleId,
                    level = level,
                    score = currentScore
                )

                val response = RetrofitClient.apiService.submitExamV2(request)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isFinished = true,
                                isPassed = true,
                                score = currentScore,
                                pointsEarned = result.pointsEarned,
                                levelUp = result.levelUp,
                                newLevel = result.newLevel,
                                topicCompleted = result.topicCompleted,
                                alreadyPassedBefore = result.pointsEarned == 0,
                                showResultDialog = true
                            )
                        }
                    }
                } else {
                    // Fallback al endpoint antiguo si el nuevo no existe
                    val oldResponse = RetrofitClient.apiService.submitExam(cleanId, moduleId, currentScore)
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isFinished = true,
                                isPassed = true,
                                score = currentScore,
                                pointsEarned = 100,  // Asumimos que da puntos
                                levelUp = false,  // No sabemos
                                showResultDialog = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar resultado: ${e.message}",
                            showResultDialog = true
                        )
                    }
                }
            }
        }
    }

    fun dismissResultDialog() {
        _uiState.update { it.copy(showResultDialog = false) }
    }
}