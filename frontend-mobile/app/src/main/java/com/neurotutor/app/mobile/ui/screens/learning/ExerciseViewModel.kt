package com.neurotutor.app.mobile.ui.screens.learning

import android.util.Log
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

data class ExerciseUiState(
    val isLoading: Boolean = false,
    val theoryHtml: String = "",
    val exercises: List<Exercise> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val isTutorVisible: Boolean = false,
    val tutorMessage: String = "",
    val totalPointsEarned: Int = 0,
    val isFinished: Boolean = false,
    val errorMessage: String? = null
)

class ExerciseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    fun loadExercises(moduleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getLevelContent(moduleId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                theoryHtml = data.teoriaHtml,
                                exercises = data.ejercicios 
                            ) 
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(isLoading = false, errorMessage = "Error al cargar contenido.") 
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }

    fun submitAnswer(selectedIndex: Int, studentId: String) {
        val currentState = _uiState.value
        if (currentState.exercises.isEmpty()) return
        
        val currentExercise = currentState.exercises[currentState.currentExerciseIndex]

        if (selectedIndex == currentExercise.correctAnswerIndex) {
            _uiState.update { 
                it.copy(
                    totalPointsEarned = it.totalPointsEarned + currentExercise.points,
                    isTutorVisible = false
                ) 
            }
            goToNextExercise(studentId)
        } else {
            _uiState.update { 
                it.copy(
                    isTutorVisible = true,
                    tutorMessage = currentExercise.tutorExplanation
                ) 
            }
        }
    }

    private fun goToNextExercise(studentId: String) {
        val currentState = _uiState.value
        if (currentState.currentExerciseIndex < currentState.exercises.size - 1) {
            _uiState.update { it.copy(currentExerciseIndex = it.currentExerciseIndex + 1) }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val cleanStudentId = studentId.replace("\"", "").trim()
                    RetrofitClient.apiService.addPoints(cleanStudentId, currentState.totalPointsEarned)
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isFinished = true) }
                    }
                } catch (e: Exception) {
                    Log.e("ExerciseVM", "Error al guardar puntos: ${e.message}")
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isFinished = true) }
                    }
                }
            }
        }
    }

    fun hideTutor() {
        _uiState.update { it.copy(isTutorVisible = false) }
    }
}
