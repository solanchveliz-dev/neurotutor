package com.neurotutor.app.mobile.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.Exercise
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

    fun nextQuestion(studentId: String, moduleId: String) {
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        } else {
            finishExam(studentId, moduleId)
        }
    }

    private fun finishExam(studentId: String, moduleId: String) {
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

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cleanId = studentId.replace("\"", "").trim()
                RetrofitClient.apiService.submitExam(cleanId, moduleId, currentScore)
                
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isFinished = true, 
                            score = currentScore, 
                            isPassed = passed 
                        ) 
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al guardar resultado.") }
                }
            }
        }
    }
}
