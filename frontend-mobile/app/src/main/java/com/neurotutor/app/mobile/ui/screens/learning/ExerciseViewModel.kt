package com.neurotutor.app.mobile.ui.screens.learning

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.local.ProgressManager
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
    val isTutorLoading: Boolean = false,
    val totalPointsEarned: Int = 0,
    val isFinished: Boolean = false,
    val errorMessage: String? = null
)

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val progressManager = ProgressManager(getApplication())
    private var currentModuleId: String = ""
    private var currentLevel: String = ""

    fun loadExercises(moduleId: String, level: String) {
        currentModuleId = moduleId
        currentLevel = level
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getLevelContent(moduleId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // ✅ LOG DE VERIFICACIÓN
                    data.ejercicios.forEachIndexed { index, exercise ->
                        Log.d("DEBUG_SUBTEMA", "Ejercicio [$index] ID: ${exercise.id} -> Subtema recibido: ${exercise.subtema}")
                    }

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
                            it.copy(isLoading = false, errorMessage = "Error al cargar contenido: ${response.code()}")
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
            // ✅ Respuesta Correcta
            progressManager.saveExerciseCompleted(studentId, currentModuleId, currentExercise.id)

            _uiState.update {
                it.copy(
                    totalPointsEarned = it.totalPointsEarned + currentExercise.points,
                    isTutorVisible = false,
                    tutorMessage = ""
                )
            }

            goToNextExercise(studentId)
        } else {
            // ❌ Respuesta Incorrecta
            val explanation = if (!currentExercise.tutorExplanation.isNullOrBlank()) {
                currentExercise.tutorExplanation
            } else {
                "¡Casi lo logras! Revisa la teoría y vuelve a intentarlo. 💪"
            }

            _uiState.update {
                it.copy(
                    isTutorVisible = true,
                    tutorMessage = explanation,
                    isTutorLoading = false
                )
            }
        }
    }

    fun goToNextExercise(studentId: String) {
        val currentState = _uiState.value
        if (currentState.currentExerciseIndex < currentState.exercises.size - 1) {
            _uiState.update {
                it.copy(
                    currentExerciseIndex = it.currentExerciseIndex + 1,
                    isTutorVisible = false,
                    tutorMessage = "",
                    isTutorLoading = false
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val cleanStudentId = studentId.replace("\"", "").trim()
                    
                    // ✅ VALIDACIÓN: Evitar puntos duplicados
                    val alreadyClaimed = progressManager.isRewardClaimed(cleanStudentId, currentModuleId, currentLevel)
                    
                    if (!alreadyClaimed) {
                        Log.d("REWARD", "Otorgando puntos por primera vez para nivel $currentLevel")
                        RetrofitClient.apiService.addPoints(cleanStudentId, currentState.totalPointsEarned)
                        progressManager.markRewardAsClaimed(cleanStudentId, currentModuleId, currentLevel)
                    } else {
                        Log.d("REWARD", "Recompensa ya reclamada para este nivel. Omitiendo addPoints.")
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isFinished = true) }
                    }
                } catch (e: Exception) {
                    Log.e("ExerciseViewModel", "Error al procesar finalización: ${e.message}")
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isFinished = true) }
                    }
                }
            }
        }
    }

    fun hideTutor() {
        _uiState.update { it.copy(isTutorVisible = false, tutorMessage = "", isTutorLoading = false) }
    }
}