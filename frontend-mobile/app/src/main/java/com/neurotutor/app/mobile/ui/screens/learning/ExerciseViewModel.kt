package com.neurotutor.app.mobile.ui.screens.learning

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.local.ProgressManager
import com.neurotutor.app.mobile.data.model.learning.Exercise
import com.neurotutor.app.mobile.data.model.learning.PracticeAnswerRequest
import com.neurotutor.app.mobile.data.model.learning.SubmitPracticeAttemptRequest
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
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isSubmitting: Boolean = false,
    val isFinished: Boolean = false,
    val errorMessage: String? = null
)

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val progressManager = ProgressManager(getApplication())
    private var currentModuleId: String = ""

    fun loadExercises(moduleId: String) {
        currentModuleId = moduleId
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                ExerciseUiState(isLoading = true)
            }
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
                    selectedAnswers = it.selectedAnswers + (currentExercise.id to selectedIndex),
                    isTutorVisible = false,
                    tutorMessage = ""
                )
            }
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
            submitPracticeAttempt(studentId, currentState)
        }
    }

    private fun submitPracticeAttempt(studentId: String, currentState: ExerciseUiState) {
        val cleanStudentId = studentId.replace("\"", "").trim().toLongOrNull()
        val cleanModuleId = currentModuleId.trim().toLongOrNull()
        if (cleanStudentId == null || cleanModuleId == null) {
            _uiState.update {
                it.copy(errorMessage = "No se pudo identificar al estudiante o al módulo.")
            }
            return
        }

        val answers = currentState.exercises.mapNotNull { exercise ->
            currentState.selectedAnswers[exercise.id]?.let { selectedIndex ->
                exercise.id.toLongOrNull()?.let { exerciseId ->
                    PracticeAnswerRequest(
                        exerciseId = exerciseId,
                        selectedAnswerIndex = selectedIndex
                    )
                }
            }
        }
        if (answers.size != currentState.exercises.size) {
            _uiState.update {
                it.copy(errorMessage = "Faltan respuestas válidas para registrar la práctica.")
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.submitPracticeAttempt(
                    SubmitPracticeAttemptRequest(
                        studentId = cleanStudentId,
                        moduloId = cleanModuleId,
                        answers = answers
                    )
                )

                val result = response.body()
                if (response.isSuccessful && result != null) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                totalPointsEarned = result.pointsEarned,
                                isFinished = true
                            )
                        }
                    }
                } else {
                    showPracticeSubmissionError()
                }
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "Error al registrar práctica", e)
                showPracticeSubmissionError()
            }
        }
    }

    private suspend fun showPracticeSubmissionError() {
        withContext(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = "No se pudo guardar la práctica. Inténtalo nuevamente."
                )
            }
        }
    }

    fun clearSubmissionError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun hideTutor() {
        _uiState.update { it.copy(isTutorVisible = false, tutorMessage = "", isTutorLoading = false) }
    }
}
