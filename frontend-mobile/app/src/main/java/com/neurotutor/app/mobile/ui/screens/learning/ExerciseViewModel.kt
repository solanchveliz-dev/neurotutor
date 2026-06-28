package com.neurotutor.app.mobile.ui.screens.learning

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.local.ProgressManager
import com.neurotutor.app.mobile.data.model.learning.Exercise
import com.neurotutor.app.mobile.data.network.GeminiService
import com.neurotutor.app.mobile.data.model.learning.PreguntaPractica
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
    private val geminiService = GeminiService()
    private var currentModuleId: String = ""

    fun loadExercises(moduleId: String) {
        currentModuleId = moduleId
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getLevelContent(moduleId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    
                    // ✅ LOG DE VERIFICACIÓN: Confirmamos que cada ejercicio trae su subtema real
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
            val respuestaUsuario = currentExercise.options[selectedIndex]
            val respuestaCorrecta = currentExercise.options[currentExercise.correctAnswerIndex]

            _uiState.update { it.copy(isTutorLoading = true) }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.e("GEMINI_TEST", "📢 Llamando a explicarError de Gemini")
                    val mensajePersonalizado = geminiService.explicarError(
                        pregunta = currentExercise.question,
                        respuestaUsuario = respuestaUsuario,
                        respuestaCorrecta = respuestaCorrecta,
                        explicacionOriginal = currentExercise.tutorExplanation
                    )

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isTutorVisible = true,
                                tutorMessage = mensajePersonalizado,
                                isTutorLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GEMINI_TEST", "❌ Error en Gemini: ${e.message}")
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isTutorVisible = true,
                                tutorMessage = "¡Casi lo logras! La respuesta correcta era $respuestaCorrecta. Revisa la teoría y vuelve a intentarlo. 💪",
                                isTutorLoading = false
                            )
                        }
                    }
                }
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
                    RetrofitClient.apiService.addPoints(cleanStudentId, currentState.totalPointsEarned)
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isFinished = true) }
                    }
                } catch (e: Exception) {
                    Log.e("GEMINI_TEST", "❌ Error al guardar puntos: ${e.message}")
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

    // ==================== MÉTODOS PARA TUTOR IA ====================

    fun generarExplicacionGemini(
        pregunta: String,
        opciones: List<String>,
        respuestaCorrecta: String,
        onResult: (String) -> Unit
    ) {
        Log.e("GEMINI_TEST", "🎯 generarExplicacionGemini llamado")
        viewModelScope.launch(Dispatchers.IO) {
            val explicacion = geminiService.generarExplicacion(pregunta, opciones, respuestaCorrecta)
            withContext(Dispatchers.Main) {
                onResult(explicacion)
            }
        }
    }

    fun generarPistaGemini(
        pregunta: String,
        opciones: List<String>,
        onResult: (String) -> Unit
    ) {
        Log.e("GEMINI_TEST", "💡 generarPistaGemini llamado")
        viewModelScope.launch(Dispatchers.IO) {
            val pista = geminiService.generarPista(pregunta, opciones)
            withContext(Dispatchers.Main) {
                onResult(pista)
            }
        }
    }

    fun generarEjerciciosSimilaresGemini(
        preguntaOriginal: String,
        tema: String,
        onResult: (List<PreguntaPractica>) -> Unit
    ) {
        Log.e("GEMINI_TEST", "📝 generarEjerciciosSimilaresGemini llamado")
        viewModelScope.launch(Dispatchers.IO) {
            val preguntas = geminiService.generarPreguntasPractica(preguntaOriginal, tema)
            withContext(Dispatchers.Main) {
                onResult(preguntas)
            }
        }
    }
}
